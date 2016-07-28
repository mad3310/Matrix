package com.letv.common.util.jacksonext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.letv.common.util.jacksonext.annotation.IncludeProperty;
import com.letv.common.util.jacksonext.annotation.JsonFilterProperties;
import com.letv.common.util.jacksonext.helper.ThreadJacksonMixInHolder;
import com.letv.common.util.jacksonext.impl.Jackson1JavassistFilterPropertyHandler;
import com.letv.common.util.jacksonext.impl.JavassistFilterPropertyHandler;
class Group {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
class User {
    private int id;
    private String name;
    private Group group;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
public class JsonFilterPropertyTest {

	@JsonFilterProperties(includes = @IncludeProperty(pojo = User.class, names = "group"))
	public Collection<User> listUsers() {
		Group group1 = new Group();
		group1.setId(1);
		group1.setName("分组1");

		User user1 = new User();
		user1.setId(1);
		user1.setGroup(group1);
		user1.setName("用户1");
		User user2 = new User();
		user2.setId(1);
		user2.setGroup(group1);
		user2.setName("用户1");
		User user3 = new User();
		user3.setId(1);
		user3.setName("用户1");
		user3.setGroup(group1);

		Group group2 = new Group();
		group2.setId(2);
		group2.setName("分组2");

		User user4 = new User();
		user4.setId(4);
		user4.setGroup(group2);
		user4.setName("用户4");
		User user5 = new User();
		user5.setId(5);
		user5.setGroup(group2);
		user5.setName("用户5");
		User user6 = new User();
		user6.setId(6);
		user6.setName("用户6");
		user6.setGroup(group2);

		Collection<User> users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		users.add(user6);
		return users;
	}

	@Test
	public void jsonTest() throws NoSuchMethodException,
			IOException {
		FilterPropertyHandler filterPropertyHandler = new JavassistFilterPropertyHandler(
				false);
		Object object = listUsers();

		object = filterPropertyHandler.filterProperties(
				JsonFilterPropertyTest.class.getMethod("listUsers"), object);

		ObjectMapper mapper = ThreadJacksonMixInHolder.builderMapper();
		String json = mapper.writeValueAsString(object);
		System.out.println(json);
	}

	@Test
	public void json1Test() throws NoSuchMethodException, IOException {
		FilterPropertyHandler filterPropertyHandler = new Jackson1JavassistFilterPropertyHandler();
		Object object = listUsers();

		object = filterPropertyHandler.filterProperties(
				JsonFilterPropertyTest.class.getMethod("listUsers"), object);

		org.codehaus.jackson.map.ObjectMapper mapper = ThreadJacksonMixInHolder
				.builderCodehausMapper();
		String json = mapper.writeValueAsString(object);
		System.out.println(json);
	}
}
