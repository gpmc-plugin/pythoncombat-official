package me.pythontest.pythoncombat.statics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class CombatMenagerMessages {
    public static Component groupCreate(String name){
        return Component.text("Utworzono grupę "+name).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component groupDelete(String name){
        return Component.text("Usunięto grupę "+name).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component groupNotExist(String name){
        return Component.text("Grupa "+name+" nie istnieje.").color(TextColor.fromHexString("#FF0000"));
    }
    public static Component groupConfigUpdated(String name,String value){
        return Component.text("Pomtślnie zaktualizowano wartość "+name+" ustawiona została na: "+value).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component groupUserAdd(String name,String gname){
        return Component.text("Pomtślnie dodano użytkownia "+name+" do grupy "+gname).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component groupUserRemove(String name,String gname){
        return Component.text("Pomtślnie usunięto użytkownia "+name+" z grupy "+gname).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component groupRegionAdd(String name,String gname){
        return Component.text("Pomtślnie dodano region "+name+" do grupy "+gname).color(TextColor.fromHexString("#32CD32"));
    }
    public static Component groupRegionDelete(String name,String gname){
        return Component.text("Pomtślnie usunięto region "+name+" z grupy "+gname).color(TextColor.fromHexString("#32CD32"));
    }
}
