package me.cuna.paramalegends.party;

import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Party {

    private final Set<PlayerParama> members = new HashSet<>();
    private final PartyManager manager;

    public Party(PartyManager manager){
        this.manager = manager;
    }
    public Set<PlayerParama> getMembers(){
        return members;
    }

    public void invite(PlayerParama invited, PlayerParama inviter){
        invited.getPlayer().sendMessage(ChatColor.GOLD + inviter.getPlayer().getName() + " has invited you to their party. Type '/party accept' to join.");
        invited.setPartyInvited(this);
    }

    public void leave(PlayerParama playerParama){
        members.remove(playerParama);
        playerParama.setParty(null);
        for(PlayerParama member : members){
            member.getPlayer().sendMessage(ChatColor.RED+ playerParama.getPlayer().getName() + " has left your party.");
        }
    }

    public void kick(PlayerParama playerParama){
        members.remove(playerParama);
        playerParama.setParty(null);
        for(PlayerParama member : members){
            member.getPlayer().sendMessage(ChatColor.RED+ playerParama.getPlayer().getName() + " has been kicked from your party.");
        }
    }
}
