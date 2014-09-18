package model;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_USERS;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.google.gson.reflect.TypeToken;


public class MyRepositoriesService extends RepositoryService {
	
	public MyRepositoriesService(GitHubClient client) {
		super(client);
	}
	
	public List<Repository> getStarredRepositories(User user) throws IOException{
		PagedRequest<Repository> req = createPagedRequest();
		
		StringBuilder uri = new StringBuilder(SEGMENT_USERS);
		uri.append("/").append(user.getLogin()).append('/').append("starred");
		req.setUri(uri);
		req.setType(new TypeToken<List<Repository>>() {
		}.getType());
		
		return getAll(req);
	}
}
