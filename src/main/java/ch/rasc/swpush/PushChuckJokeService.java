package ch.rasc.swpush;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ch.rasc.swpush.fcm.FcmClient;

@Service
public class PushChuckJokeService {

  private final FcmClient fcmClient;


  private int seq = 0;

  public PushChuckJokeService(FcmClient fcmClient) {
    this.fcmClient = fcmClient;
  }

  @Scheduled(fixedDelay = 30_000)
  public void sendChuckQuotes() {
      IcndbJoke joke = new RestTemplate().getForEntity("http://api.icndb.com/jokes/random",IcndbJoke.class).getBody();
    try {
      sendPushMessage(joke);
    }
    catch (InterruptedException | ExecutionException e) {
      Application.logger.error("send chuck joke", e);
    }
  }

  void sendPushMessage(IcndbJoke joke) throws InterruptedException, ExecutionException {
    Map<String, String> data = new HashMap<>();
    data.put("id", String.valueOf(joke.getValue().getId()));
    data.put("joke", joke.getValue().getJoke());
    data.put("seq", String.valueOf(this.seq++));
    data.put("ts", String.valueOf(System.currentTimeMillis()));

    System.out.println("Sending chuck joke...");
    this.fcmClient.send(data);
  }

}
