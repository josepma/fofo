/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fofo.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author portatil
 */
@Entity
@Table (name="CLUB")
public class Club {
    @Id
    @Column (name="NAME")
    private String name;
    private String email;
     
    @OneToMany (mappedBy="club", cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
    private List<Team> teams;
    
    /**
     *
     */
    public Club(){
        this.teams = new ArrayList<Team>();
        name = "";
        email="";
        
    }
    
    /**
     *
     * @param name
     */
    public Club(String name){
        this.teams = new ArrayList<Team>();
        this.name = name;
        email = "";
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     *
     * @param teams
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    
    @Override
    public boolean equals(Object obj){
        
        return (obj instanceof Club) && 
                ((Club) obj).name.equals(this.name) &&
                ((Club) obj).email.equals(this.email) &&
                ((Club) obj).teams.size() == this.teams.size() &&
                 equalTeamNames(((Club) obj).teams,this.teams);
                
    }
    
    @Override
    public String toString(){
     
        return "Club name="+name + "teams="+teams;
        
    }




    private boolean equalTeamNames(List<Team> teams1, List<Team> teams2){
 
      for (Team team: teams1){
          
          if (! findName(team.getName(),teams2)) return false;
          
      }    
      return true;
      
          
    }

    private boolean findName(String name, List<Team> teams){
     
        for (Team team : teams){
          if ( name.equals(team.getName())) return true;
                   
        }    
        return false;
    
    }   

 
}