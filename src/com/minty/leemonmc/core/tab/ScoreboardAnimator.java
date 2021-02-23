package com.minty.leemonmc.core.tab;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.core.CoreMain;

public class ScoreboardAnimator {

	private CoreMain main = CoreMain.getInstance();
    
	int cooldown = 3;
    int ipCharIndex = 0;
	
    private long updateTime = 2;
	
    private String result;
    
	public void setup()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				stepAnimation();
			}
		}.runTaskTimer(main, updateTime, updateTime);
	}
	
	private void stepAnimation()
	{
		result = doAnimate(ChatColor.GOLD, ChatColor.YELLOW, ChatColor.WHITE);
	}
	
	public String getFooter() {
		return result;
	}
	
	public long getUpdateTime() {
		return updateTime;
	}
	
	public String doAnimate(ChatColor color1, ChatColor color2, ChatColor color3) {
        StringBuilder formattedIp = new StringBuilder();
        String ip = "play.leemonmc.fr";
        
        if (cooldown > 0) {
            cooldown-= 1;
            return color1 + ip;
        }
        if (ipCharIndex > 0) {
            formattedIp.append(ip.substring(0, ipCharIndex - 1));
            formattedIp.append(color2).append(ip.substring(ipCharIndex - 1, ipCharIndex));
        } else {
            formattedIp.append(ip.substring(0, ipCharIndex));
        }

        formattedIp.append(color3).append(ip.charAt(ipCharIndex));

        if (ipCharIndex + 1 < ip.length()) {
            formattedIp.append(color2).append(ip.charAt(ipCharIndex + 1));

            if (ipCharIndex + 2 < ip.length())
                formattedIp.append(color1).append(ip.substring(ipCharIndex + 2));

            ipCharIndex++;
        } else {
            ipCharIndex = 0;
            cooldown = 50;
        }
        return color1 + formattedIp.toString();
    }
	
}
