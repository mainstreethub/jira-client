/**
 * jira-client - a simple JIRA REST client
 * Copyright (c) 2013 Bob Carroll (bob.carroll@alum.rit.edu)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.rcarz.jiraclient;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a JIRA user.
 */
public class User extends Resource {

    private boolean active = false;
    private Map<String, String> avatarUrls = null;
    private String displayName = null;
    private String email = null;
    private String accountId = null;

    /**
     * Creates a user from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json       JSON payload
     */
    protected User(RestClient restclient, JSONObject json) {
        super(restclient);

        if (json != null)
            deserialise(json);
    }

    /**
     *  Retrieves list of user records by query (name, username, email)
     *  See (https://developer.atlassian.com/cloud/jira/platform/rest/?#api-api-2-user-search-get)
     *
     * @param restclient REST client instance
     * @param attribute The attribute for the search (name, username, email)
     * @return a list of user instances
     * @throws JiraException when the retrieval fails
     */
    public static List<User> search(RestClient restclient, String attribute) throws JiraException {
        List<User> result = new ArrayList<>();
        JSON response = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("query", attribute);

        try {
            response = restclient.get(getBaseUri() + "user/search", params);
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve user with attribute " + attribute, ex);
        }

        if (!(response instanceof JSONArray))
            throw new JiraException("JSON payload is malformed");

        ((JSONArray) response).forEach(
                j -> result.add(new User(restclient, (JSONObject) j))
        );
        return result;
    }

    public static User create(RestClient restClient, String email, String displayName) throws JiraException{
        JSON result;

        JSONObject params = new JSONObject();
        params.put("email", email);
        params.put("displayName", displayName);

        try {
            result = restClient.post("/rest/servicedeskapi/customer", params);
        } catch (Exception ex) {
            if(ex instanceof RestException && ((RestException) ex).getHttpStatusCode() == 400) {
                //user already existed in the system
                List<User> searchResult = search(restClient, email);
                if (searchResult.size() > 0) {
                    return searchResult.get(0);
                } else {
                    // the user won't be found if they're inactive in jira but can't be created with dupe email
                    return null;
                }
            } else {
                throw new JiraException("Failed to create user with params " + params.toString(), ex);
            }
        }

        if (!(result instanceof JSONObject))
            throw new JiraException("JSON payload is malformed");

        return new User(restClient, (JSONObject) result);

    }

    private void deserialise(JSONObject json) {
        Map map = json;

        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        active = Field.getBoolean(map.get("active"));
        avatarUrls = Field.getMap(String.class, String.class, map.get("avatarUrls"));
        displayName = Field.getString(map.get("displayName"));
        email = getEmailFromMap(map);
        accountId = Field.getString(map.get("accountId"));
    }

    /**
     * API changes email address might be represented as either "email" or "emailAddress"
     *
     * @param map JSON object for the User
     * @return String email address of the JIRA user.
     */
    private String getEmailFromMap(Map map) {
        if (map.containsKey("email")) {
            return Field.getString(map.get("email"));
        } else {
            return Field.getString(map.get("emailAddress"));
        }
    }

    public boolean isActive() {
        return active;
    }

    public Map<String, String> getAvatarUrls() {
        return avatarUrls;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return getAccountId();
    }
}

