package journeybuddy.spring.config.OAuth2;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import journeybuddy.spring.config.AppConfig;
import journeybuddy.spring.domain.user.Role;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.user.RoleRepository;
import journeybuddy.spring.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class KaKaoService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final String clientId;

    public KaKaoService(UserRepository userRepository, RoleRepository roleRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.clientId = appConfig.getKakaoClientId();
    }

    public String getToken(String code) throws Exception {
        String access_Token = "";

        //EndPoint URL = API가 서버에서 자원에 접근할 수 있도록 하는 URL
        final String requestUrl = "https://kauth.kakao.com/oauth/token";

        //토큰을 요청할 URL 객체 생성
        URL url = new URL(requestUrl);

        //HTTP 연결 설정
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        //서버로 요청 보내기
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));


        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id=").append(clientId);
        sb.append("&redirect_uri=http://localhost:3000/journeybuddy/oauth");
        sb.append("&code=" + code);
        bw.write(sb.toString());
        bw.flush();

        //서버의 응답 데이터 가져옴
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line = "";
        String result = "";

        //result에 토큰이 포함된 응답데이터를 한줄씩 저장
        while ((line = br.readLine()) != null) {
            result += line;
        }

        //JSON 데이터를 파싱하기 위한 JsonParser
        JsonParser parser = new JsonParser();
        //값 추출을 위해 파싱한 데이터를 JsonElement로 변환
        JsonElement element = parser.parse(result);

        //element에서 access_token 값을 얻어옴
        access_Token = element.getAsJsonObject().get("access_token").getAsString();

        br.close();
        bw.close();

        return access_Token;

    }

    public User getUserInfo(String accessToken) throws Exception {
        final String requestUrl = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = bf.readLine()) != null) {
            response.append(line);
        }

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(response.toString());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject properties = jsonObject.has("properties") ? jsonObject.getAsJsonObject("properties") : new JsonObject();
        JsonObject kakaoAccount = jsonObject.has("kakao_account") ? jsonObject.getAsJsonObject("kakao_account") : new JsonObject();

        String thumbnailImage = properties.has("thumbnail_image") ? properties.get("thumbnail_image").getAsString() : null;
        String profileImage = properties.has("profile_image") ? properties.get("profile_image").getAsString() : null;
        String nickname = properties.has("nickname") ? properties.get("nickname").getAsString() : null;
        String email = kakaoAccount.has("email") ? kakaoAccount.get("email").getAsString() : null;
        String birthday = kakaoAccount.has("birthday") ? kakaoAccount.get("birthday").getAsString() : null;

        log.info("nickname:{}",nickname);
        log.info("birthday:{}",birthday);
        log.info("profileImage:{}",profileImage);
        log.info("email:{}",email);
        log.info("사용자썸네일프로필사진:{}",thumbnailImage);

        
        // DB에서 사용자 조회 및 저장
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        //role권한부여 추가(현재는 role 공백이어도 로그인가능하게 처리해놓음)
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = Role.builder().name("USER").build();
            roleRepository.save(userRole);
        }

        if (!existingUser.isPresent()) {
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));

            user = User.builder()
                    .profile_image(profileImage)
                    .nickname(nickname)
                    .email(email)
                    .roles(Collections.singletonList(defaultRole))
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get();
            user.setProfile_image(profileImage);
            user.setNickname(nickname);
            userRepository.save(user);  // 업데이트된 정보 저장
        }
        return user;
    }
}