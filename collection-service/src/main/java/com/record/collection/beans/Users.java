package com.record.collection.beans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="USERS")
public class Users implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "USERNAME")
	private String username;
	@Column(name = "PASSWORD")
	private String password;
	@Column(name = "ROLE")
	private String role;
	@Column(name = "ENABLED")
	private boolean enabled;
	@Column(name="ACCESS_LEVEL")
	private String accessLevel;
	
	@OneToMany(mappedBy="createdBy",targetEntity=CollectionInfo.class, fetch=FetchType.EAGER)
	private Set<CollectionInfo> collectionInfo;
	
	@OneToMany(mappedBy="createdBy",targetEntity=ExpenseInfo.class, fetch=FetchType.EAGER)
	private Set<ExpenseInfo> expenseInfo;
	
	@OneToMany(mappedBy="createdBy",targetEntity=EarningPlan.class, fetch=FetchType.EAGER)
	private Set<EarningPlan> earningPlan;
	
	@OneToMany(mappedBy="createdBy",targetEntity=Budget.class, fetch=FetchType.EAGER)
	private Set<Budget> budget;

	public Set<ExpenseInfo> getExpenseInfo() {
		return expenseInfo;
	}

	public void setExpenseInfo(Set<ExpenseInfo> expenseInfo) {
		this.expenseInfo = expenseInfo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<CollectionInfo> getCollectionInfo() {
		return collectionInfo;
	}

	public void setCollectionInfo(Set<CollectionInfo> collectionInfo) {
		this.collectionInfo = collectionInfo;
	}

	public Set<EarningPlan> getEarningPlan() {
		return earningPlan;
	}

	public void setEarningPlan(Set<EarningPlan> earningPlan) {
		this.earningPlan = earningPlan;
	}

	public Set<Budget> getBudget() {
		return budget;
	}

	public void setBudget(Set<Budget> budget) {
		this.budget = budget;
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", username=" + username + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", collectionInfo=" + collectionInfo + "]";
	}

}
