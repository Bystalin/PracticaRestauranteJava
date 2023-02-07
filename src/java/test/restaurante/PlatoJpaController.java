/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.restaurante;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import test.restaurante.exceptions.NonexistentEntityException;
import test.restaurante.exceptions.PreexistingEntityException;
import test.restaurante.exceptions.RollbackFailureException;

/**
 *
 * @author Byron-PC
 */
public class PlatoJpaController implements Serializable {

    public PlatoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Plato plato) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Categoria idcategoria = plato.getIdcategoria();
            if (idcategoria != null) {
                idcategoria = em.getReference(idcategoria.getClass(), idcategoria.getId());
                plato.setIdcategoria(idcategoria);
            }
            em.persist(plato);
            if (idcategoria != null) {
                idcategoria.getPlatoList().add(plato);
                idcategoria = em.merge(idcategoria);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPlato(plato.getId()) != null) {
                throw new PreexistingEntityException("Plato " + plato + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Plato plato) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Plato persistentPlato = em.find(Plato.class, plato.getId());
            Categoria idcategoriaOld = persistentPlato.getIdcategoria();
            Categoria idcategoriaNew = plato.getIdcategoria();
            if (idcategoriaNew != null) {
                idcategoriaNew = em.getReference(idcategoriaNew.getClass(), idcategoriaNew.getId());
                plato.setIdcategoria(idcategoriaNew);
            }
            plato = em.merge(plato);
            if (idcategoriaOld != null && !idcategoriaOld.equals(idcategoriaNew)) {
                idcategoriaOld.getPlatoList().remove(plato);
                idcategoriaOld = em.merge(idcategoriaOld);
            }
            if (idcategoriaNew != null && !idcategoriaNew.equals(idcategoriaOld)) {
                idcategoriaNew.getPlatoList().add(plato);
                idcategoriaNew = em.merge(idcategoriaNew);
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
                Integer id = plato.getId();
                if (findPlato(id) == null) {
                    throw new NonexistentEntityException("The plato with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Plato plato;
            try {
                plato = em.getReference(Plato.class, id);
                plato.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The plato with id " + id + " no longer exists.", enfe);
            }
            Categoria idcategoria = plato.getIdcategoria();
            if (idcategoria != null) {
                idcategoria.getPlatoList().remove(plato);
                idcategoria = em.merge(idcategoria);
            }
            em.remove(plato);
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

    public List<Plato> findPlatoEntities() {
        return findPlatoEntities(true, -1, -1);
    }

    public List<Plato> findPlatoEntities(int maxResults, int firstResult) {
        return findPlatoEntities(false, maxResults, firstResult);
    }

    private List<Plato> findPlatoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Plato.class));
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

    public Plato findPlato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Plato.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlatoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Plato> rt = cq.from(Plato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
