package org.fofo.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.fofo.entity.Category;
import org.fofo.entity.Club;
import org.fofo.entity.Competition;
import org.fofo.entity.FCalendar;
import org.fofo.entity.Match;
import org.fofo.entity.Team;
import org.fofo.entity.WeekMatches;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.fofo.entity.*;

/**
 *
 * @author David Hernández
 * @author Anton Urrea
 */
@RunWith(JMock.class)
public class CalendarDAOImplTest {

    Mockery context = new JUnit4Mockery();
    EntityManager em;
    EntityTransaction transaction;
    CalendarDAOImpl calDAO;
    CalendarDAO caldao;
    Match match1, match2, match3, match4;
    WeekMatches wm1, wm2;
    FCalendar cal;
    Club club;
    Team team1, team2, team3, team4;
    private Competition comp;
    private TeamDAO tdao;

    @Before
    public void setUp() throws Exception {

        em = context.mock(EntityManager.class);
        transaction = context.mock(EntityTransaction.class);
        caldao = context.mock(CalendarDAO.class);
        tdao = context.mock(TeamDAO.class);


        calDAO = new CalendarDAOImpl();
        calDAO.setEm(em);
        
        calDAO.setTd(tdao);

        club = new Club("ClubExemple");
        club.setEmail("exemple@hotmail.com");


        team1 = new Team("Team1", Category.FEMALE);
        team1.setClub(club);
        team1.setEmail("Team1@hotmail.com");

        team2 = new Team("Team2", Category.FEMALE);
        team2.setClub(club);
        team2.setEmail("Team2@hotmail.com");

        team3 = new Team("Team3", Category.FEMALE);
        team3.setClub(club);
        team3.setEmail("Team3@hotmail.com");

        team4 = new Team("Team4", Category.FEMALE);
        team4.setClub(club);
        team4.setEmail("Team4@hotmail.com");


        match1 = new Match();
        match1.setLocal(team1);
        match1.setVisitant(team2);

        match2 = new Match();
        match2.setLocal(team3);
        match2.setVisitant(team4);

        match3 = new Match();
        match3.setLocal(team2);
        match3.setVisitant(team1);

        match4 = new Match();
        match4.setLocal(team4);
        match4.setVisitant(team3);

        wm1 = new WeekMatches();
        wm1.addMatch(match1);
        wm1.addMatch(match2);

        wm2 = new WeekMatches();
        wm2.addMatch(match3);
        wm2.addMatch(match4);

        comp = Competition.create(Type.CUP);
        comp.setCategory(Category.FEMALE);
        comp.setInici(new Date());
        comp.setMaxTeams(4);
        comp.setMinTeams(2);
        comp.setName("Lleida");

        cal = new FCalendar();
        cal.setCompetition(comp);

    }

    /**
     * Add Calendar with only one WeekMatch with only one Match.
     *
     * @throws Exception
     */
    //@Test
    public void testAdditionOfJustOneMatch() throws Exception {
        wm1.addMatch(match1);

        cal.getAllWeekMatches().add(wm1);

        context.checking(new Expectations() {

            {
                atLeast(1).of(em).getTransaction();
                will(returnValue(transaction));
                oneOf(transaction).begin();
                // oneOf(em).getTransaction();
                //   will(returnValue(transaction));
                oneOf(transaction).commit();
                oneOf(em).persist(match1);
                oneOf(em).persist(wm1);
                oneOf(em).persist(cal);
            }
        });

        calDAO.addCalendar(cal);
    }

    /**
     * Add Calendar with only one WeekMatch with various Match.
     *
     * @throws Exception
     */
    //@Test
    public void testAdditionOfVariousMatchesOneWM() throws Exception {

        wm1.addMatch(match1);
        wm1.addMatch(match2);
        wm2.addMatch(match3);
        wm2.addMatch(match4);

        cal.getAllWeekMatches().add(wm1);
        cal.getAllWeekMatches().add(wm2);

        context.checking(new Expectations() {

            {
                oneOf(em).getTransaction();
                will(returnValue(transaction));
                oneOf(transaction).begin();
                oneOf(em).getTransaction();
                will(returnValue(transaction));
                oneOf(transaction).commit();
                oneOf(em).persist(match1);
                oneOf(em).persist(match2);
                oneOf(em).persist(match3);
                oneOf(em).persist(match4);
                oneOf(em).persist(wm1);
                oneOf(em).persist(cal);
            }
        });

        calDAO.addCalendar(cal);
    }

    /**
     * Add Calendar with only various WeekMatch with various Match.
     *
     * @throws Exception
     */
    //@Test
    public void testAddVariousWeekMatches() throws Exception {

        List<WeekMatches> Lwm = null;

        wm1.addMatch(match1);
        wm1.addMatch(match2);
        wm2.addMatch(match3);
        wm2.addMatch(match4);

        Lwm.add(wm1);
        Lwm.add(wm2);

        cal.setWeekMatches(Lwm);

        context.checking(new Expectations() {

            {
                oneOf(em).getTransaction();
                will(returnValue(transaction));
                oneOf(transaction).begin();
                oneOf(em).getTransaction();
                will(returnValue(transaction));
                oneOf(transaction).commit();
                oneOf(em).persist(match1);
                oneOf(em).persist(match2);
                oneOf(em).persist(wm1);
                oneOf(em).persist(match3);
                oneOf(em).persist(match4);
                oneOf(em).persist(wm2);
                oneOf(em).persist(cal);
            }
        });

        calDAO.addCalendar(cal);
    }

    @Test
    public void CorrectAddMatch() throws Exception {
        context.checking(new Expectations() {

            {
                oneOf(tdao).findTeam(match1.getLocal());  will(returnValue(true));
                oneOf(tdao).findTeam(match1.getVisitant()); will(returnValue(true));
                oneOf(em).persist(match1);
            }
        });


        calDAO.addMatch(match1);
    }
    
    
    @Test
    public void CorrectAddWeekMatches() throws Exception {
        
        context.checking(new Expectations() {

            {
                for (int i = 0; i < wm1.getNumberOfMatchs(); i++) {
                  //  System.out.println("GetNumberOfMatch--> "+wm1.getNumberOfMatchs());
                    oneOf(tdao).findTeam(wm1.getListOfWeekMatches().get(i).getLocal());  will(returnValue(true));
                    oneOf(tdao).findTeam(wm1.getListOfWeekMatches().get(i).getVisitant()); will(returnValue(true));
                    oneOf(em).persist(wm1.getListOfWeekMatches().get(i));
                }
                oneOf(em).persist(wm1);
            }
        });


        calDAO.addWeekMatches(wm1);
    }
    
    //@Test
    public void CorrectAddCalenadr() throws Exception {
        
      cal.getAllWeekMatches().add(wm1);

   
        context.checking(new Expectations() {

            {
                     oneOf(em).getTransaction().begin();
                     
                      for (int i = 0; i < cal.getNumOfWeekMatches(); i++) {
                           System.out.println("i: "+i);
                           
                        for (int x = 0; x < cal.getWeekMatch(i).getNumberOfMatchs(); x++) {
                            System.out.println("x: "+x);
                            oneOf(tdao).findTeam(cal.getWeekMatch(i).getListOfWeekMatches().get(x).getLocal());  will(returnValue(true));
                            oneOf(tdao).findTeam(cal.getWeekMatch(i).getListOfWeekMatches().get(x).getVisitant()); will(returnValue(true));
                            oneOf(em).persist(cal.getWeekMatch(i).getListOfWeekMatches().get(x));
                        }
                        oneOf(em).persist(cal.getWeekMatch(i).getNumberOfMatchs());
                     }         
                      
                     oneOf(em).persist(cal);
                     oneOf(em).getTransaction().commit();
            }
        });
        
         calDAO.addCalendar(cal);
        
    }
    
    
    
   
}