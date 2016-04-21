package com.webcomm.oa.jazz.data;

import com.ibm.team.repository.common.UUID;
import com.ibm.team.scm.common.IVersionable;

public class FileData {
	private UUID itemId = null;
	private UUID stateId = null;
	private IVersionable item = null;
	private String name = null;
	private String fullName = null;
	private int kind = -1;
	
	public FileData() {
		
	}
	
	
	
	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}

	public UUID getStateId() {
		return stateId;
	}

	public void setStateId(UUID stateId) {
		this.stateId = stateId;
	}

	public IVersionable getItem() {
		return item;
	}

	public void setItem(IVersionable item) {
		this.item = item;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("fullName = ")
			.append(fullName)
			.append(", name = ")
			.append(name)
			.append(", kind = ")
			.append(kind);
		return sb.toString();
	}
	
	
}
