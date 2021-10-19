package shoe.store.server.payload.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {
	private String accessToken;
	private String type = "Bearer";
	private String id;
	private String username;
	private List<String> roles;

	public JwtResponse(String accessToken, String id, String username, List<String> roles) {
		this.accessToken = accessToken;
		this.id = id;
		this.username = username;
		this.roles = roles;
	}

}
