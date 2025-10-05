package pl.jakubholik90;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;

import java.util.Optional;

public class ChatClient {

        private final OpenAIClient client;

        public ChatClient() {
            this.client = OpenAIOkHttpClient.builder()
                    .apiKey(PrivateData.apiKey)
                    .build();
        }

        public ResponseRecord chatRequest(RequestRecord requestRecord, String additionalInstructions) {
           String instructionsText = "talk in a humorous way. talk in polish. " + additionalInstructions;

            Optional<String> instructionsOptional = Optional.of(instructionsText);

            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_5_NANO)
                    .input(requestRecord.input())
                    // .previousResponseId(requestRecord.previousId())
                    .instructions(instructionsOptional)
                    .build();

            Response response = this.client.responses().create(params);

            String responseText = response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(item -> item.content().stream())
                    .flatMap(item -> item.outputText().stream())
                    .map(ResponseOutputText::text)
                    .toList()
                    .getFirst();
            return new ResponseRecord(responseText,response.previousResponseId().toString());
        }

}
