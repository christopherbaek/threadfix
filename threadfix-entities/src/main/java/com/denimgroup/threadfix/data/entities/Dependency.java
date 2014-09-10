////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.data.entities;

import com.denimgroup.threadfix.views.AllViews;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Dependency")
public class Dependency extends AuditableEntity {

	private static final long serialVersionUID = 3647499545381978852L;
	
	@Size(max = 20, message = "{errors.maxlength} 20.")
	private String cve;

    @Size(max = 1024)
    private String componentName = null;

    @Size(max = 1024)
    private String componentFilePath = null;

    @Size(max = 1024000)
    private String description = null;

    @Nullable
    @Column(nullable = true)
    @JsonView(AllViews.UIVulnSearch.class)
    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Nullable
    @Column(nullable = true)
    public String getComponentFilePath() {
        return componentFilePath;
    }

    public void setComponentFilePath(String componentFilePath) {
        this.componentFilePath = componentFilePath;
    }

    @Nullable
    @Column(nullable = true)
    @JsonView(AllViews.UIVulnSearch.class)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length = 20)
    @JsonView(Object.class)
    public String getCve() {
        return cve;
    }

    public void setCve(String cve) {
        this.cve = cve;
    }

    /**
     * This is used to identify the dependency in an unambiguous way.
     * @return
     */
    @Transient
    @JsonIgnore
    public String getKey() {
        return componentName + " - " + getCve();
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "cve='" + cve + '\'' +
                ", componentName='" + componentName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
