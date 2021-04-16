package com.springboot.contactSaver.helper;

public class Message {
	public String content;
	public String type;

	public Message() {
		super();
	}

	@Override
	public String toString() {
		return "Message [content=" + content + ", type=" + type + "]";
	}

	public Message(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
}
