package net.rcarz.jiraclient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

public class UserTest {

    private java.lang.String accountId = "7de3fbbc-4609-4a52-b1a4-5b8eaf894175";
    private java.lang.String displayName = "Joseph McCarthy";
    private java.lang.String email = "joseph.b.mccarthy2012@googlemail.com";
    private java.lang.String userID = "10";
    private boolean isActive = true;
    private String self = "https://brainbubble.atlassian.net/rest/api/2/user?username=joseph";

    @Test
    public void testJSONDeserializer() throws IOException, URISyntaxException {
        User user = new User(new RestClient(null, new URI("/123/asd")), getTestJSON());
        assertEquals(user.getAccountId(), accountId);
        assertEquals(user.getDisplayName(), displayName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getId(), userID);

        Map<String, String> avatars = user.getAvatarUrls();

        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16", avatars.get("16x16"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24", avatars.get("24x24"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32", avatars.get("32x32"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48", avatars.get("48x48"));

        assertTrue(user.isActive());
    }

    private JSONObject getTestJSON() {
        JSONObject json = new JSONObject();

        json.put("accountId", accountId);
        json.put("email", email);
        json.put("active", isActive);
        json.put("displayName", displayName);
        json.put("self", self);

        JSONObject images = new JSONObject();
        images.put("16x16", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16");
        images.put("24x24", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24");
        images.put("32x32", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32");
        images.put("48x48", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48");

        json.put("avatarUrls", images);
        json.put("id", "10");

        return json;
    }

    private JSONArray getTestJSONArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(getTestJSON());

        return jsonArray;
    }

    @Test
    public void testStatusToString() throws URISyntaxException {
        User user = new User(new RestClient(null, new URI("/123/asd")), getTestJSON());
        assertEquals(accountId, user.toString());
    }


    @Test(expected = JiraException.class)
    public void testSearchJSONError() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString(),anyMap())).thenReturn(null);
        User.search(restClient, "email");

    }

    @Test(expected = JiraException.class)
    public void testSearchError() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString(),anyMap())).thenThrow(Exception.class);
        User.search(restClient, "email");
    }

    @Test
    public void testSearchNoResults() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString(), anyMap())).thenReturn(new JSONArray());

        final List<User> users = User.search(restClient, "email");

        assertEquals(users.size(), 0);
    }

    @Test
    public void testSearch() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString(), anyMap())).thenReturn(getTestJSONArray());

        final List<User> users = User.search(restClient, "email");
        User user = users.get(0);

        assertEquals(user.getAccountId(), accountId);
        assertEquals(user.getDisplayName(), displayName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getId(), userID);

        Map<String, String> avatars = user.getAvatarUrls();

        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16", avatars.get("16x16"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24", avatars.get("24x24"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32", avatars.get("32x32"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48", avatars.get("48x48"));

        assertTrue(user.isActive());
    }

    @Test
    public void testCreate() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.post(anyString(), any(JSONObject.class))).thenReturn(getTestJSON());

        final User user = User.create(restClient, "email", "displayName");

        assertEquals(user.getAccountId(), accountId);
        assertEquals(user.getDisplayName(), displayName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getId(), userID);

        Map<String, String> avatars = user.getAvatarUrls();

        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16", avatars.get("16x16"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24", avatars.get("24x24"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32", avatars.get("32x32"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48", avatars.get("48x48"));

        assertTrue(user.isActive());

    }

    @Test
    public void testCreate_doesNotThrowExceptionOn400Response() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.post(anyString(), any(JSONObject.class))).thenThrow(new RestException("msg", 400, "result", new Header[0]));
        when(restClient.get(anyString(), anyMap())).thenReturn(getTestJSONArray());


        final User user = User.create(restClient, "email", "displayName");

        assertEquals(user.getAccountId(), accountId);
        assertEquals(user.getDisplayName(), displayName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getId(), userID);

        Map<String, String> avatars = user.getAvatarUrls();

        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16", avatars.get("16x16"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24", avatars.get("24x24"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32", avatars.get("32x32"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48", avatars.get("48x48"));

        assertTrue(user.isActive());

    }

    @Test(expected = JiraException.class)
    public void testCreate_doesNotThrowExceptionOnOtherResponse() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.post(anyString(), any(JSONObject.class))).thenThrow(new RestException("msg", 500, "result", new Header[0]));

        User.create(restClient, "email", "displayName");
    }

}
