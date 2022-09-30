package me.pythontest.pythoncombat.statics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.DecimalFormat;

public class ActionBarMessages {
    public static Component Timeoutbar(double now,double max){
        Component message = Component.empty();
        Component nowText  = Component.text(new DecimalFormat("##.#").format(now));
        Component maxText = Component.text(" / "+max);
        nowText = nowText.color(TextColor.fromHexString("#ff0000"));
        message = message.append(nowText);
        message = message.append(maxText);
        return  message;
    }
    public static Component PvpStatus(Player player,boolean status){
        NamedTextColor color = status?NamedTextColor.GREEN: NamedTextColor.RED;
        return Component.text("Status pvp "+player.getName()+" jest ustawiony na: "+(status?"allow":"deny"),color);
    }
}
