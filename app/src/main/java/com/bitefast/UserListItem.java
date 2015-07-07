package com.bitefast;

public class UserListItem {
	public boolean readStatus;
	public String message;

	public UserListItem(boolean readStatus, String message) {
		super();
		this.readStatus = readStatus;
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		UserListItem temp=(UserListItem)o;
		return temp.message.equals(this.message);
	}
}