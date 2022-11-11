package me.pythontest.pythoncombat.statics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import javax.naming.Name;

public class ChatMessages {
    public static Component CantFight(Player damaged){
        Component message = Component.empty();
        Component cantDamageText = Component.text("Nie możesz walczyć z "+damaged.getName()+". Jeżeli chcesz z nim walczyć ");
        cantDamageText = cantDamageText.color(TextColor.fromHexString("#AA0000"));
        Component duelInvite = Component.text("zaproś go do duel");
        duelInvite = duelInvite.style(Style.style(TextDecoration.UNDERLINED));
        duelInvite = duelInvite.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/duel invite "+damaged.getName()));
        message = message.append(cantDamageText);
        message = message.append(duelInvite);
        message = message.hoverEvent(HoverEvent.showText(Component.text("Jeżeli nie chcesz otrzymywać więcej takich wiadomości kliknij ją")));
        message = message.clickEvent(ClickEvent.runCommand("/notificationsettings cantfight deny"));
        return message;
    }
    public static Component PCinfo(){
        Component message = Component.text("Autorem pluginu pythoncombat 1.2.2(rewolucja naolityczna relase) jest pythontest\n");
        Component line2 = Component.text("Projekt jest udostępniany na licencji GPL-3.0\n");
        Component line3 = Component.text("Kod można znaleźć ").append(Component.text("tutaj", NamedTextColor.WHITE,TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/gpmc-plugin/pythoncombat-official"))).append(Component.text(". A prywatne gh autora ")).append(Component.text("tutaj",NamedTextColor.WHITE,TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/mikimasn")));
        Component line4 = Component.text("\nPlugin wykorzystuje różne inne projekty opensource oto one: \n");
        Component line5 = Component.text("- org.openjfx jfx\n", NamedTextColor.WHITE,TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/openjdk/jfx"));
        Component line6 = Component.text("- io.papermc.paper paper-api\n",NamedTextColor.WHITE,TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/PaperMC/Paper"));
        Component line7 = Component.text("- com.sk89q.worldguard worldguard-bukkit\n", NamedTextColor.WHITE,TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/EngineHub/WorldGuard/tree/master/worldguard-bukkit"));
        Component line8 = Component.text("Dzięki temu oprogramowaniu pythoncombat miał szanse powstać bez niego raczej by to sie nie udało.");
        message = message.append(line2).append(line3).append(line4).append(line5).append(line6).append(line7).append(line8);
        return message;
    }
    public static Component NotPlayer(){
        return Component.text("Aby użyć tej komendy musisz być graczem");
    }
    public static Component LowArgs(){
        return Component.text("Za mało argumentów");
    }
    public static Component PvpUpdate(String state){
        return Component.text("Pomyślnie zmieniono status pvp na: "+state).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component UnknownUser(String username){
        return Component.text("Nieznana nazwa użytkownika: "+username).color(TextColor.fromHexString("#FF0000"));
    }
    public static Component DuelInviteCreated(String username){
        Component message = Component.empty();
        Component cantDamageText = Component.text("Użytkownik "+username+" zaprosił cię do duel. Jeżeli chcesz z nim walczyć ");
        cantDamageText = cantDamageText.color(TextColor.fromHexString("#32CD32"));
        Component duelInvite = Component.text("kliknij tutaj");
        duelInvite = duelInvite.style(Style.style(TextDecoration.UNDERLINED));
        duelInvite = duelInvite.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,"/duel accept "+username));
        message = message.append(cantDamageText);
        message = message.append(duelInvite);
        return message;
    }
    public static Component DuelInviteSended(String username){
        return Component.text("Wysłano zaproszenie do walki do użytkownika "+username).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component DuelInviteFailed(){
        return Component.text("Nie udało się wyslać zaproszenia").color(TextColor.fromHexString("#ff0000"));
    }
    public static Component DuelInviteNotExist(String username){
        return Component.text("Zaproszenie od użytkownika "+username+" nie istnieje").color(TextColor.fromHexString("#ff0000"));
    }
    public static Component DuelInviteAccepted(String username,Boolean to){
        return Component.text("Zaproszenie "+(to?"do":"od")+" użytkownika "+username+" zostało zaakceptowane. Możecie walczyć").color(TextColor.fromHexString("#32CD32"));
    }
    public static Component DuelInviteDeclined(String username){
        return Component.text("Pomyślnie odrzucono zaproszenie od "+username).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component DuelNotExist(String username){
        return Component.text("Duel z użytkownikiem "+username+" nie istnieje").color(TextColor.fromHexString("#ff0000"));
    }
    public static Component DuelRemove(String username){
        return Component.text("Duel z użytkownikiem "+username+" został usunięty").color(TextColor.fromHexString("#ff0000"));
    }
    public static Component Error(){
        return Component.text("W trakcie przetwarzania twojego żądania wstąpił błąd").color(TextColor.fromHexString("#ff0000"));
    }
    public static Component CombatKill(String deathusername, String killerusername){
        return Component.text(deathusername+" został zabity przez pythoncombat próbując uciec przed "+killerusername);
    }
    public static Component valueNotFound(String name){
        return Component.text("Nie można ustawić wartości na: "+name).color(TextColor.fromHexString("#ff0000"));
    }
    public static Component timeoutRemoved(String name){
        return Component.text("Combatlog z użytkownikiem: "+name+" został usunięty. Pamiętaj że możesz mieć combat log jeszcze z innymi użytkownikami!!").color(TextColor.fromHexString("#ff0000"));
    }
}
