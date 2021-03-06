/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fofo.dao;

import org.fofo.dao.exception.AlreadyExistingClubOrTeamsException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.fofo.entity.Club;
import org.fofo.entity.Team;

/**
 *
 * @author ruffolution, Anatoli, Mohamed
 */
public class ClubDAOImpl implements ClubDAO {

    private EntityManager em;
    
    public ClubDAOImpl() {
    }

    /**
     *
     * @param em
     */
    public void setEM(EntityManager em) {
        this.em = em;
    }

    /**
     *
     * @return
     */
    public EntityManager getEM() {
        return this.em;
    }
    
    /**
     *
     * @param club
     * @throws Exception
     */
    @Override
    public void addClub(Club club) throws AlreadyExistingClubOrTeamsException {
        
        
        try {
            em.getTransaction().begin();
            
            checkExistingClubOrTeams(club);

            em.persist(club);
            em.getTransaction().commit();
            
        } catch (PersistenceException e) {}
    }

    /**
     *
     * @param name
     */
    @Override
    public void removeClub(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<Club> getClubs() {  
        List<Club> clubs = null;
        Query query;
        try{
            em.getTransaction().begin();
            query = em.createQuery("SELECT c FROM Club c");
            clubs = (List<Club>) query.getResultList();
            em.getTransaction().commit();            
        }catch(PersistenceException e){}
        
        return clubs;
    }

    /**
     *
     * @param name
     * @return
     * @throws PersistenceException
     */
    @Override
    public Club findClubByName(String name) {
        Club club = null;
       
        em.getTransaction().begin();
        club = (Club) em.find(Club.class, name);
        em.getTransaction().commit();
        
        return club;
    }

    /**
     *
     * @param name
     * @return
     * @throws PersistenceException
     */
    @Override
    public Club findClubByTeam(String name) {
        Team team = null;
        
        em.getTransaction().begin();
        team = (Team) em.find(Team.class, name);
        em.getTransaction().commit();
               
        return findClubByName(team.getClub().getName());
    }
    
    /* PRIVATE OPS */

    private boolean clubExist(Club club) {
        return em.find(Club.class, club.getName())!=null;
    }

    private boolean teamsExist(List<Team> teams) {
        for(Team t : teams){
            Team team = (Team) em.find(Team.class, t.getName());
            if(team != null)
                return true;
        }
        
        return false;
    }
    

    private void checkExistingClubOrTeams(Club club) throws AlreadyExistingClubOrTeamsException {
        if(clubExist(club))
            throw new AlreadyExistingClubOrTeamsException("This club "
                    +club.getName()+" already exist in DB");
        
        if(teamsExist(club.getTeams()))
            throw new AlreadyExistingClubOrTeamsException("One or more teams "
                    +"of this club "+club.getName()+" already exist in DB");
            
    }
}
