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
@Table(name="CONTRIBUTOR")
public class Contributor implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private String id;
	
	@Column(name = "CONTRIBUTOR_NAME")
	private String contributorName;
	@Column(name = "CONTRIBUTOR_FULL_NAME")
	private String contributorFullName;
	@Column(name = "ASSOCIATION")
	private String association;	
	@Column(name = "EMAIL")
	private String email;	
	@Column(name = "ACTIVE")
	private boolean active;	
	@Column(name = "DISPLAY_NAME")
	private String displayName;
	
	@OneToMany(mappedBy="contributor",targetEntity=CollectionInfo.class, fetch=FetchType.EAGER)
	private Set<CollectionInfo> collectionInfo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContributorName() {
		return contributorName;
	}
	public void setContributorName(String contributorName) {
		this.contributorName = contributorName;
	}
	public String getContributorFullName() {
		return contributorFullName;
	}
	public void setContributorFullName(String contributorFullName) {
		this.contributorFullName = contributorFullName;
	}
	public String getAssociation() {
		return association;
	}
	public void setAssociation(String association) {
		this.association = association;
	}
	public Set<CollectionInfo> getCollectionInfo() {
		return collectionInfo;
	}
	public void setCollectionInfo(Set<CollectionInfo> collectionInfo) {
		this.collectionInfo = collectionInfo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "Contributer [id=" + id + ", contributerName=" + contributorName + ", contributerFullName="
				+ contributorFullName + ", association=" + association + ", collectionInfo=" + collectionInfo + "]";
	}
}
