package org.fofo.dao;

import javax.persistence.EntityManager;
import org.fofo.entity.FCalendar;

/**
 *
 * @author David Hernández
 * @author Anton Urrea
 */
public interface CalendarDAO {

    void addCalendar(FCalendar cal) throws IncorrectTeamException, PersistException;

    TeamDAO getTd();

    void setTd(TeamDAO td);

    EntityManager getEm();

    void setEm(EntityManager em);
}
