package com.letv.portal.controller.cloudswift;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class FolderModel {
    private String file;
    private String directory;
    
    
    @NotBlank
	@Length(min=1,max=254)
	@Pattern(regexp = "^[a-zA-Z0-9\u4e00-\u9fa5_.-]*$", message = "只能包含字母，数字，中文，下划线（_）和短横线（-）,小数点（.）")
    public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	

	
}
