

public class QuizModel {
    private String question;
    private String[] options;
    private int correctOption;

    /**
     * Constructor to initialize question, options and correct option index.
     * @param question প্রশ্ন
     * @param options অপশনগুলোর অ্যারে (চারটি অপশন)
     * @param correctOption সঠিক অপশনের ইনডেক্স (1-based index: 1, 2, 3, or 4)
     */
    public QuizModel(String question, String[] options, int correctOption) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    /**
     * প্রশ্ন রিটার্ন করে।
     */
    public String getQuestion() {
        return question;
    }

    /**
     * অপশন অ্যারে রিটার্ন করে।
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * সঠিক অপশনের ইনডেক্স (1-based) রিটার্ন করে।
     */
    public int getCorrectOption() {
        return correctOption;
    }
}
