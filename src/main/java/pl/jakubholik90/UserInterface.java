package pl.jakubholik90;

import java.util.*;

public class UserInterface {

    static Scanner scanner = new Scanner(System.in);

    private static ChatClient chatClient = new ChatClient();

    public static void runApp() {

        String previousId = greetUser().previousId();
        String name = scanner.nextLine();

        ResponseRecord generateCategoriesRecord = generateCategories(3, previousId);
        previousId = generateCategoriesRecord.previousId();
        String generateCategoriesString = generateCategoriesRecord.response();
        List<String> generateCategoriesList = Arrays.stream(generateCategoriesString.split(",")).toList();
        HashMap<Integer, String> jokeTypes = new HashMap<>();
        for (int i = 1; i < generateCategoriesList.size()+1; i++) {
            jokeTypes.put(i,generateCategoriesList.get(i-1));
        }
        System.out.println("...");
        previousId=presentOptions(jokeTypes,null, name).previousId();
        int currentOption = scanner.nextInt();

        Set<Integer> jokeCategories = jokeTypes.keySet();

        boolean categoryOK = (jokeCategories.contains(currentOption));

        while (!categoryOK) {
            System.out.println("...");
            badNumber(jokeTypes,previousId,currentOption);
            currentOption = scanner.nextInt();
            categoryOK = (jokeCategories.contains(currentOption));
        }

        System.out.println("...");
        sayJoke(jokeTypes,previousId,currentOption);
    }

    private static ResponseRecord greetUser() {
        ResponseRecord responseRecord = chatClient.chatRequest(new RequestRecord("hi",null), "ask for the name");
        System.out.println(responseRecord.response());
        return new ResponseRecord(responseRecord.response(), responseRecord.previousId());
    }

    private static ResponseRecord generateCategories(int numberOfCategories, String previousId) {
        ResponseRecord responseRecord = chatClient.chatRequest(new RequestRecord("generate " + numberOfCategories + "joke categories",previousId), "show categories only, as comma (,) separated values");
        return new ResponseRecord(responseRecord.response(), responseRecord.previousId());
    }

    private static ResponseRecord presentOptions(HashMap<Integer, String> optionsMap, String previousId, String userName) {
        Set<Integer> jokeNumbers = optionsMap.keySet();
        List<String> optionsList = new ArrayList<>(jokeNumbers.size());
        for (Integer jokeNumber : jokeNumbers) {
            optionsList.add(jokeNumber + ": " + optionsMap.get(jokeNumber));
        }
        String optionsString = optionsList.toString();

        ResponseRecord responseRecord = chatClient.chatRequest(new RequestRecord(optionsString,previousId), "ask user to choose between one of given options. ask him to press a key with proper number in order to choose an option. name of user is " + userName + ". add one more fake joke option with number " + (optionsMap.size()+1) + ". don't tell that this is a fake category");
        System.out.println(responseRecord.response());
        return new ResponseRecord(responseRecord.response(), responseRecord.previousId());
    }

    private static ResponseRecord sayJoke(HashMap<Integer, String> optionsMap, String previousId, int currentOption) {
        String input = "say a joke within this category: " + optionsMap.get(currentOption);
        ResponseRecord responseRecord = chatClient.chatRequest(new RequestRecord(input,previousId), "don't propose next joke. say goodbye");
        System.out.println(responseRecord.response());
        return new ResponseRecord(responseRecord.response(), responseRecord.previousId());
    }

    private static ResponseRecord badNumber(HashMap<Integer, String> optionsMap, String previousId, int currentOption) {
        String fakeCategory = null;
        if (currentOption==optionsMap.size()+1) {
            fakeCategory = "tell, that choosen category was a joke already";
        } else {
            fakeCategory = " inform user, that " + currentOption + "is wrong. ";
        }
        String input = fakeCategory + "ask user to choose between one of following options " + optionsMap.keySet();
        ResponseRecord responseRecord = chatClient.chatRequest(new RequestRecord(input,previousId), "");
        System.out.println(responseRecord.response());
        return new ResponseRecord(responseRecord.response(), responseRecord.previousId());
    }
}
