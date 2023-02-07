/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.restaurante;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Byron-PC
 */
@Stateless
public class PlatoFacade extends AbstractFacade<Plato> {

    @PersistenceContext(unitName = "TestRestauranteUniPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlatoFacade() {
        super(Plato.class);
    }
    
}
