package com.record.collection.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="BUDGET")
public class Budget implements Serializable
{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private String id;
	@Column(name = "BUDGET_GROUP")
	private String budgetGroup;
	@Column(name = "BUDGET_TYPE")
	private String budgetType;
	@Column(name = "BUDGET_YEAR")
	private int budgetYear;
	@Column(name = "AMOUNT")
	private double amount;
	@Column(name = "IS_SAVING")
	private boolean isSavingAmount;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	@ManyToOne(optional=false)
	@JoinColumn(name="CREATED_BY", referencedColumnName="USERNAME")
	private Users createdBy;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBudgetGroup() {
		return budgetGroup;
	}
	public void setBudgetGroup(String budgetGroup) {
		this.budgetGroup = budgetGroup;
	}
	public String getBudgetType() {
		return budgetType;
	}
	public void setBudgetType(String budgetType) {
		this.budgetType = budgetType;
	}
	public int getBudgetYear() {
		return budgetYear;
	}
	public void setBudgetYear(int budgetYear) {
		this.budgetYear = budgetYear;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public boolean isSavingAmount() {
		return isSavingAmount;
	}
	public void setIsSavingAmount(boolean isSavingAmount) {
		this.isSavingAmount = isSavingAmount;
	}
	public String getDescription() {
		return (description == null || description.length() < 1) ? "N/A" : description;
	}
	public void setDescription(String description) {
		this.description = (description == null || description.length() < 1) ? "N/A" : description;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Users getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Users createdBy) {
		this.createdBy = createdBy;
	}
}
