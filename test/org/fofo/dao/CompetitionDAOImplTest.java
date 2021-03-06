/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fofo.dao;

import org.fofo.dao.exception.InvalidCompetitionException;
import org.fofo.dao.exception.IncorrectTeamException;
import org.fofo.dao.exception.PersistException;
import org.fofo.dao.exception.InvalidTeamException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.fofo.entity.ClassificationTC;
import org.fofo.entity.Club;
import org.fofo.entity.Competition;
import org.fofo.entity.CompetitionLeague;
import org.fofo.entity.CompetitionType;
import org.fofo.entity.Team;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 * @author Alejandro
 */
@RunWith(JMock.class)
public class CompetitionDAOImplTest {

    Mockery context = new JUnit4Mockery();
    EntityManager em;
    EntityTransaction transaction;
    CompetitionDAOImpl competitionDAO;
    Team team;
    Competition competition;
    Club club;

    @Before
    public void setUp() {

        em = context.mock(EntityManager.class);
        transaction = context.mock(EntityTransaction.class);
        competitionDAO = new CompetitionDAOImpl();
        team = new Team("name");
        competition = new CompetitionLeague();
        competition.setName("LeagueOne");
        club = new Club("name");
        club.setEmail("email");
        competitionDAO.setEM(em);
        team.setClub(club);

    }

    @Test
    public void addCompetition() throws PersistException {

        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                oneOf(transaction).commit();
                oneOf(em).persist(competition);
            }
        });

        competitionDAO.addCompetition(competition);
    }

      @Test
    public void addTeamAndClassificationTCToCompetition() throws Exception {
          
          final ClassificationTC classif = new ClassificationTC (competition,team);

          context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                    will(returnValue(transaction));                    
                oneOf(em).find(Team.class, team.getName());
                    will(returnValue(team));
                oneOf(em).find(Competition.class, competition.getName());
                    will(returnValue(competition));
                oneOf(transaction).begin();
                oneOf(transaction).commit();
                oneOf(em).persist(classif);
            }
        });

        competitionDAO.addTeam(competition, team);
    } 

    @Test(expected = InvalidTeamException.class)
    public void add_incorrect_team_to_competition() throws Exception {
        
          context.checking(new Expectations() {
            {
                atLeast(1).of (em).getTransaction();will(returnValue(transaction));
                oneOf(transaction).begin();

            }
        });

        competitionDAO.addTeam(competition, null);
        
    }
    
    @Test(expected=InvalidCompetitionException.class)
    public void addTeamtoIncorrectCompetition() throws Exception{
        
          context.checking(new Expectations() {
            {
                atLeast(1).of (em).getTransaction();will(returnValue(transaction));
                oneOf(transaction).begin();

            }
        });
        competitionDAO.addTeam(null, team);
    }
    
   
    /*
     * 
     * TESTS FOR CLASSIFICATIONS
     * 
     * 
     */

    @Test(expected=IncorrectTeamException.class)
    public void classificationOfANonExistingTeam() throws Exception{
        
        final Team team = new Team("team10");
        final Competition comp = Competition.create(CompetitionType.LEAGUE);
        comp.setName("comp10");
        comp.setMinTeams(5);
        comp.setMaxTeams(10);
        
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                allowing(transaction).commit();
                oneOf(em).find(Competition.class,"comp10");
                        will (returnValue(comp));
                oneOf(em).find(Team.class,"team10"); 
                        will (returnValue(null));
                
            }
        });
                
            
          competitionDAO.addClassificationTC("team10", "comp10");  
                
    }

    @Test(expected=InvalidCompetitionException.class)
    public void classificationOfANonExistingComp() throws Exception{
        
        final Team team = new Team("team10");
        final Competition comp = Competition.create(CompetitionType.LEAGUE);
        comp.setName("comp10");
        comp.setMinTeams(5);
        comp.setMaxTeams(10);
        
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                allowing(transaction).commit();
                oneOf(em).find(Competition.class,"comp10");
                        will (returnValue(null));
                oneOf(em).find(Team.class,"team10"); 
                        will (returnValue(team));
                
            }
        });
                
            
          competitionDAO.addClassificationTC("team10", "comp10");  
                
    }

    @Test(expected=IncorrectTeamException.class)
    public void classificationInsertionOfATeamNotInCompetition() throws Exception{
        
        final Team team = new Team("team10");
        final Competition comp = Competition.create(CompetitionType.LEAGUE);
        comp.setName("comp10");
        comp.setMinTeams(5);
        comp.setMaxTeams(10);

        //Notice that team is not assigned to the competition comp.
        
        final ClassificationTC classif = new ClassificationTC (comp,team);
        classif.setPoints(0);

        
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                allowing(transaction).commit();
                oneOf(em).find(Competition.class,"comp10");
                        will (returnValue(comp));
                oneOf(em).find(Team.class,"team10"); 
                        will (returnValue(team));
                allowing(em).persist(classif);
                    //***NOTE THAT TO MAKE THIS WORK, IT IS NECESSARY TO
                    //***OVERWRITE ClassificationTC.equals()
            }
        });
                
            
          competitionDAO.addClassificationTC("team10", "comp10");  
                
    }

    
    
    @Test
    public void classificationInsertion() throws Exception{
        
        final Team team = new Team("team10");
        final Competition comp = Competition.create(CompetitionType.LEAGUE);
        comp.setName("comp10");
        comp.setMinTeams(5);
        comp.setMaxTeams(10);
        comp.getTeams().add(team);
        
        final ClassificationTC classif = new ClassificationTC (comp,team);
        classif.setPoints(0);

        
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                allowing(transaction).commit();
                oneOf(em).find(Competition.class,"comp10");
                        will (returnValue(comp));
                oneOf(em).find(Team.class,"team10"); 
                        will (returnValue(team));
                oneOf(em).persist(classif);
                    //***NOTE THAT TO MAKE THIS WORK, IT IS NECESSARY TO
                    //***OVERWRITE ClassificationTC.equals()
            }
        });
                
            
          competitionDAO.addClassificationTC("team10", "comp10");  
                
    }
    
    
    
    @Test(expected=InvalidCompetitionException.class)
    public void getClassificationOfANonExistingComp() throws Exception{
        
        final Competition comp = Competition.create(CompetitionType.LEAGUE);
        comp.setName("comp10");
        comp.setMinTeams(5);
        comp.setMaxTeams(10);
        
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                allowing(transaction).commit();
                oneOf(em).find(Competition.class,"comp10");
                        will (returnValue(null));
                
            }
        });
                
            
          competitionDAO.findClassificationsTC("comp10");  
                
    }

    @Test
    public void getClassificationCorrect() throws Exception{
        
        
        
        final Competition comp = Competition.create(CompetitionType.LEAGUE);
        comp.setName("comp10");
        comp.setMinTeams(5);
        comp.setMaxTeams(10);
        
        final ClassificationTC classif = new ClassificationTC (comp,team);
        classif.setPoints(0);

        final List<ClassificationTC> lclass = new ArrayList<ClassificationTC>();
        lclass.add(new ClassificationTC(comp,team));
        lclass.add(new ClassificationTC(comp,new Team("team11")));

        comp.setClassificationsTC(lclass);
        
        
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                allowing(transaction).commit();
                oneOf(em).find(Competition.class,"comp10");
                        will (returnValue(comp));

 
            }
        });
                
            
          List<ClassificationTC> lc = competitionDAO.findClassificationsTC("comp10");  

          assertEquals("Should obtain the given list",lclass,lc);
    }
    
    @Test
    public void addPointsToTeam() throws Exception{
        ClassificationTC classif = new ClassificationTC(competition, team);
        classif.setPoints(0);
        team.setClassificationsTC(Arrays.asList(classif));
        context.checking(new Expectations() {
            {
                atLeast(1).of(em).getTransaction();will(returnValue(transaction));
                oneOf(transaction).begin();
                oneOf(em).find(Competition.class,competition.getName());will (returnValue(competition));
                oneOf(em).find(Team.class, team.getName());will(returnValue(team));
                allowing(transaction).commit();
            }
        });
        competitionDAO.addPointsToClassificationTC(team.getName(), competition.getName(), 3);
        assertEquals(3, classif.getPoints());
    }
    
    @Test
    public void findClassificationsTCTest() throws Exception{
        final Competition comp = Competition.create(CompetitionType.CUP);
        comp.setName("comp1");
        context.checking(new Expectations() {
            {
                oneOf(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                oneOf(em).find(Competition.class, "comp1");
                   will(returnValue(comp));
                oneOf(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).commit();
            }
        });
        
        competitionDAO.findClassificationsTC("comp1");
    }
    
    @Test(expected=InvalidCompetitionException.class)
    public void findWrongClassificationsTCTest() throws Exception{

        context.checking(new Expectations() {
            {
                oneOf(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).begin();
                oneOf(em).find(Competition.class, "comp1");
                   will(returnValue(null));
                oneOf(em).getTransaction();
                   will(returnValue(transaction));
                oneOf(transaction).commit();
            }
        });
        
        competitionDAO.findClassificationsTC("comp1");
    }
}