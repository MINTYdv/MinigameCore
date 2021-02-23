package com.minty.leemonmc.core.util;

public enum ColorCode {

	DARK_RED("§4", "\u00A74", 14),
	RED("§c", "\u00A7c", 14),
	GOLD("§6", "\u00A76", 1),
	YELLOW("§e", "\u00A7e", 4),
	DARK_GREEN("§2", "\u00A72", 13),
	GREEN("§a", "\u00A7a", 5),
	AQUA("§b", "\u00A7b", 3),
	DARK_AQUA("§3", "\u00A73", 0),
	DARK_BLUE("§1", "\u00A71", 11),
	BLUE("§9", "\u00A79", 9),
	LIGHT_PURPLE("§d", "\u00A7d", 6),
	DARK_PURPLE("§5", "\u00A75", 10),
	WHITE("§f", "\u00A7f", 0),
	GRAY("§7", "\u00A77", 8),
	DARK_GRAY("§8", "\u00A78", 7),
	BLACK("§0", "\u00A70", 15),
	RESET("§r", "\u00A7k", 0),
	UNDERLINE("§n", "\u00A7n", 0),
	STRIKETHROUGH("§m", "\u00A7m", 0),
	BOLD("§l", "\u00A7l", 0),
	OBFUSCATED("§k", "\u00A7k", 0),
	ITALIC("§o", "\u00A7o", 0);
	
	private String chatCode;
	private String MOTDCode;
	private byte WoolID;
	
	private ColorCode(String ChatCode, String MotdCode, int WoolID) {
		this.chatCode = ChatCode;
		this.MOTDCode = MotdCode;
		this.WoolID = (byte) WoolID;
	}
	
	public String getChatCode() {
		return this.chatCode;
	}
	public String getMOTDCode() {
		return this.MOTDCode;
	}
	public byte getIDCode() {
		return this.WoolID;
	}
	
}
