/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.restaurante;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import test.restaurante.exceptions.IllegalOrphanException;
import test.restaurante.exceptions.NonexistentEntityException;
import test.restaurante.exceptions.PreexistingEntityException;
import test.restaurante.exceptions.RollbackFailureException;

/**
 *
 * @author Byron-PC
 */
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (categoria.getPlatoList() == null) {
            categoria.setPlatoList(new ArrayList<Plato>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Plato> attachedPlatoList = new ArrayList<Plato>();
            for (Plato platoListPlatoToAttach : categoria.getPlatoList()) {
                platoListPlatoToAttach = em.getReference(platoListPlatoToAttach.getClass(), platoListPlatoToAttach.getId());
                attachedPlatoList.add(platoListPlatoToAttach);
            }
            categoria.setPlatoList(attachedPlatoList);
            em.persist(categoria);
            for (Plato platoListPlato : categoria.getPlatoList()) {
                Categoria oldIdcategoriaOfPlatoListPlato = platoListPlato.getIdcategoria();
                platoListPlato.setIdcategoria(categoria);
                platoListPlato = em.merge(platoListPlato);
                if (oldIdcategoriaOfPlatoListPlato != null) {
                    oldIdcategoriaOfPlatoListPlato.getPlatoList().remove(platoListPlato);
                    oldIdcategoriaOfPlatoListPlato = em.merge(oldIdcategoriaOfPlatoListPlato);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCategoria(categoria.getId()) != null) {
                throw new PreexistingEntityException("Categoria " + categoria + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categoria categoria) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getId());
            List<Plato> platoListOld = persistentCategoria.getPlatoList();
            List<Plato> platoListNew = categoria.getPlatoList();
            List<String> illegalOrphanMessages = null;
            for (Plato platoListOldPlato : platoListOld) {
                if (!platoListNew.contains(platoListOldPlato)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Plato " + platoListOldPlato + " since its idcategoria field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Plato> attachedPlatoListNew = new ArrayList<Plato>();
            for (Plato platoListNewPlatoToAttach : platoListNew) {
                platoListNewPlatoToAttach = em.getReference(platoListNewPlatoToAttach.getClass(), platoListNewPlatoToAttach.getId());
                attachedPlatoListNew.add(platoListNewPlatoToAttach);
            }
            platoListNew = attachedPlatoListNew;
            categoria.setPlatoList(platoListNew);
            categoria = em.merge(categoria);
            for (Plato platoListNewPlato : platoListNew) {
                if (!platoListOld.contains(platoListNewPlato)) {
                    Categoria oldIdcategoriaOfPlatoListNewPlato = platoListNewPlato.getIdcategoria();
                    platoListNewPlato.setIdcategoria(categoria);
                    platoListNewPlato = em.merge(platoListNewPlato);
                    if (oldIdcategoriaOfPlatoListNewPlato != null && !oldIdcategoriaOfPlatoListNewPlato.equals(categoria)) {
                        oldIdcategoriaOfPlatoListNewPlato.getPlatoList().remove(platoListNewPlato);
                        oldIdcategoriaOfPlatoListNewPlato = em.merge(oldIdcategoriaOfPlatoListNewPlato);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getId();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Plato> platoListOrphanCheck = categoria.getPlatoList();
            for (Plato platoListOrphanCheckPlato : platoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Categoria (" + categoria + ") cannot be destroyed since the Plato " + platoListOrphanCheckPlato + " in its platoList field has a non-nullable idcategoria field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(categoria);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
