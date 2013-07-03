package org.zone.commandit.python.integration;

import java.io.IOException;
import java.io.OutputStream;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrintAdapter extends OutputStream {

	private String data = "";
	private Player p;
	
	public PrintAdapter(Player p){
		this.p = p;
	}
	
	@Override
	public void write(int b) throws IOException {
		char c = (char) b;
		if(b=='\n'){
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', data));
			data = "";
		}else if (c == '\r'){
		}else{
			data += c;
		}
	}

}
