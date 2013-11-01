package com.anonymous.classes;

public class User {
	private String username;
	private String course;
	private String sex;
	
	public User () {
		this.username = this.course = this.sex = "";
	}
	
	public User (String username, String course, String sex) {
		this.username = username;
		this.course = course;
		this.sex = sex;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", course=" + course + ", sex="
				+ sex + "]";
	}
	
}
