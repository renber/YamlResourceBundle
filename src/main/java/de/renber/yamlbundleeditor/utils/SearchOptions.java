package de.renber.yamlbundleeditor.utils;

import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.models.ResourceKey;

public class SearchOptions {

	private String searchTerm = "";

	public boolean searchInNames = false;
	public boolean searchInValues = true;

	public boolean matches(ResourceKey key) {
		if (searchInNames) {
			if (!key.name.contains(searchTerm))
				return false;
		}
		if (searchInValues) {
			if (!QuIterables.query(key.getLocalizedValues()).where(x -> x.value != null).exists(x -> x.value.toString().toLowerCase().contains(searchTerm))) {
				return false;
			}
		}		

		return true;
	}

	public void setSearchTerm(String searchTerm) {
		if (searchTerm == null)
			searchTerm = "";
		else
			this.searchTerm = searchTerm.toLowerCase();
	}

	public String getSearchTerm() {
		return searchTerm;
	}
}
