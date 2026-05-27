package com.proswipe.controller;

import com.proswipe.model.SkillTest;
import com.proswipe.model.User;
import com.proswipe.repository.SkillTestRepository;
import com.proswipe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillTestController {

    @Autowired private SkillTestRepository skillTestRepository;
    @Autowired private UserRepository userRepository;

    // REQ-F-26, F-27: MCQ questions bank per skill
    private static final Map<String, List<Map<String, Object>>> QUESTION_BANK = new HashMap<>();

    static {
        // Java Questions
        List<Map<String, Object>> java = new ArrayList<>();
        java.add(q("Which keyword is used to inherit a class in Java?", "extends", "implements", "inherits", "super", 0));
        java.add(q("What is the default value of an int variable in Java?", "0", "null", "-1", "undefined", 0));
        java.add(q("Which collection class allows duplicate elements?", "HashSet", "TreeSet", "ArrayList", "LinkedHashSet", 2));
        java.add(q("What does JVM stand for?", "Java Virtual Machine", "Java Variable Method", "Java Version Manager", "Java Verified Module", 0));
        java.add(q("Which method is the entry point of a Java program?", "start()", "main()", "run()", "init()", 1));
        java.add(q("What is encapsulation in Java?", "Hiding data using access modifiers", "Multiple inheritance", "Method overriding", "Abstract classes", 0));
        java.add(q("Which operator is used for object comparison in Java?", "==", ".equals()", "===", "compare()", 1));
        java.add(q("What is the size of int in Java?", "2 bytes", "4 bytes", "8 bytes", "16 bytes", 1));
        java.add(q("Which exception is thrown when array index is out of bounds?", "NullPointerException", "ArrayIndexOutOfBoundsException", "ClassCastException", "IllegalArgumentException", 1));
        java.add(q("Which Java feature allows a method to have the same name with different parameters?", "Overriding", "Overloading", "Polymorphism", "Abstraction", 1));
        java.add(q("What is the parent class of all Java classes?", "Class", "Object", "Abstract", "Base", 1));
        java.add(q("Which interface must be implemented to create a thread in Java?", "Runnable", "Callable", "Thread", "Executable", 0));
        java.add(q("What keyword makes a variable constant in Java?", "static", "final", "const", "immutable", 1));
        java.add(q("Which collection maintains insertion order?", "HashMap", "HashSet", "LinkedList", "TreeMap", 2));
        java.add(q("What does 'static' mean in Java?", "Belongs to instance", "Belongs to class", "Cannot be changed", "Always public", 1));
        QUESTION_BANK.put("java", java);

        // Python Questions
        List<Map<String, Object>> python = new ArrayList<>();
        python.add(q("Which keyword defines a function in Python?", "function", "def", "fun", "define", 1));
        python.add(q("What is the output of type([1,2,3])?", "<class 'tuple'>", "<class 'list'>", "<class 'array'>", "<class 'dict'>", 1));
        python.add(q("Which symbol is used for single-line comments in Python?", "//", "#", "/*", "--", 1));
        python.add(q("What does len('hello') return?", "4", "5", "6", "Error", 1));
        python.add(q("Which data structure uses key-value pairs?", "list", "tuple", "set", "dictionary", 3));
        python.add(q("What is the correct way to create a list in Python?", "list = {1,2,3}", "list = [1,2,3]", "list = (1,2,3)", "list = <1,2,3>", 1));
        python.add(q("Which method adds an element to the end of a list?", "add()", "append()", "insert()", "push()", 1));
        python.add(q("What is a lambda in Python?", "A class", "An anonymous function", "A loop", "A module", 1));
        python.add(q("Which keyword is used for exception handling?", "catch", "except", "error", "handle", 1));
        python.add(q("What does 'pip' stand for?", "Python Index Package", "Pip Installs Packages", "Python Install Program", "Package Index Python", 1));
        python.add(q("Which operator is used for floor division?", "/", "//", "%", "**", 1));
        python.add(q("How do you open a file in Python?", "File.open()", "open()", "fopen()", "readFile()", 1));
        python.add(q("What is the output of 2**3?", "6", "8", "9", "5", 1));
        python.add(q("Which method removes whitespace from both ends of a string?", "trim()", "strip()", "clean()", "remove()", 1));
        python.add(q("What is a tuple?", "Mutable sequence", "Immutable sequence", "Key-value store", "Ordered set", 1));
        QUESTION_BANK.put("python", python);

        // SQL Questions
        List<Map<String, Object>> sql = new ArrayList<>();
        sql.add(q("Which SQL command retrieves data?", "INSERT", "SELECT", "UPDATE", "DELETE", 1));
        sql.add(q("Which clause filters rows in SQL?", "HAVING", "WHERE", "GROUP BY", "ORDER BY", 1));
        sql.add(q("What does PRIMARY KEY do?", "Allows NULL values", "Uniquely identifies each row", "Links two tables", "Creates an index", 1));
        sql.add(q("Which JOIN returns all rows from both tables?", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL OUTER JOIN", 3));
        sql.add(q("Which aggregate function counts rows?", "SUM()", "AVG()", "COUNT()", "MAX()", 2));
        sql.add(q("Which SQL clause is used to sort results?", "SORT BY", "ORDER BY", "GROUP BY", "FILTER BY", 1));
        sql.add(q("What does DISTINCT do?", "Removes duplicates", "Finds unique keys", "Counts rows", "Joins tables", 0));
        sql.add(q("Which command adds a new row?", "ADD", "INSERT INTO", "CREATE", "APPEND", 1));
        sql.add(q("What does FOREIGN KEY do?", "Uniquely identifies rows", "Links to another table's primary key", "Creates indexes", "Prevents NULL", 1));
        sql.add(q("Which function returns the current date?", "NOW()", "DATE()", "CURDATE()", "TODAY()", 2));
        sql.add(q("Which clause filters grouped results?", "WHERE", "FILTER", "HAVING", "LIMIT", 2));
        sql.add(q("What does TRUNCATE do?", "Deletes table structure", "Removes all rows but keeps structure", "Adds rows", "Renames table", 1));
        sql.add(q("Which SQL keyword prevents NULL values?", "UNIQUE", "NOT NULL", "DEFAULT", "PRIMARY", 1));
        sql.add(q("What does INDEX improve?", "Security", "Query performance", "Storage", "Relationships", 1));
        sql.add(q("Which command modifies existing rows?", "MODIFY", "ALTER", "UPDATE", "CHANGE", 2));
        QUESTION_BANK.put("sql", sql);

        // Spring Boot Questions
        List<Map<String, Object>> spring = new ArrayList<>();
        spring.add(q("Which annotation marks a class as a REST controller?", "@Controller", "@RestController", "@Service", "@Component", 1));
        spring.add(q("Which annotation auto-wires dependencies?", "@Inject", "@Autowired", "@Resource", "@Bean", 1));
        spring.add(q("What does @SpringBootApplication include?", "@Configuration only", "@ComponentScan only", "@Configuration, @ComponentScan, @EnableAutoConfiguration", "@Service and @Repository", 2));
        spring.add(q("Which annotation maps HTTP GET requests?", "@PostMapping", "@GetMapping", "@RequestMapping", "@FetchMapping", 1));
        spring.add(q("What is Spring Data JPA used for?", "Frontend rendering", "Database access using repositories", "Security management", "REST API creation", 1));
        spring.add(q("Which file contains Spring Boot configuration?", "config.xml", "application.properties", "settings.json", "boot.yml", 1));
        spring.add(q("What annotation makes a class a Spring bean?", "@Bean", "@Component", "@Entity", "@Service", 1));
        spring.add(q("Which port does Spring Boot use by default?", "80", "3000", "8080", "443", 2));
        spring.add(q("What does @PathVariable do?", "Maps request body", "Extracts value from URL path", "Sets response headers", "Validates input", 1));
        spring.add(q("Which annotation marks a JPA entity?", "@Table", "@Entity", "@Model", "@Data", 1));
        spring.add(q("What is @RequestBody used for?", "Sending responses", "Mapping HTTP request body to object", "Path mapping", "Header extraction", 1));
        spring.add(q("Which interface do JPA repositories extend?", "JpaInterface", "JpaRepository", "CrudRepository only", "Repository", 1));
        spring.add(q("What annotation handles exceptions globally?", "@ExceptionHandler", "@ControllerAdvice", "@ErrorHandler", "@GlobalException", 1));
        spring.add(q("What does ddl-auto=update do?", "Drops all tables", "Updates schema automatically", "Disables JPA", "Enables SQL logs", 1));
        spring.add(q("Which annotation enables CORS in Spring?", "@AllowOrigin", "@CrossOrigin", "@EnableCORS", "@CORS", 1));
        QUESTION_BANK.put("spring boot", spring);

        // JavaScript Questions
        List<Map<String, Object>> js = new ArrayList<>();
        js.add(q("Which keyword declares a block-scoped variable?", "var", "let", "dim", "variable", 1));
        js.add(q("What does === check in JavaScript?", "Value only", "Type only", "Value and type", "Reference", 2));
        js.add(q("Which method converts JSON string to object?", "JSON.parse()", "JSON.stringify()", "JSON.convert()", "JSON.decode()", 0));
        js.add(q("What is a closure?", "A loop", "Function with access to outer scope", "An array method", "A promise", 1));
        js.add(q("Which method adds element to end of array?", "push()", "append()", "add()", "insert()", 0));
        js.add(q("What does async/await do?", "Synchronous code", "Handle asynchronous operations", "Create classes", "Define variables", 1));
        js.add(q("Which event fires when DOM is fully loaded?", "onload", "DOMContentLoaded", "ready", "init", 1));
        js.add(q("What is the purpose of 'use strict'?", "Enable ES6 features", "Enforce stricter parsing", "Improve performance", "Allow global variables", 1));
        js.add(q("Which method removes last element from array?", "removeLast()", "pop()", "delete()", "splice()", 1));
        js.add(q("What does fetch() return?", "Data directly", "A Promise", "A callback", "JSON automatically", 1));
        js.add(q("Which operator creates a copy of an object?", "...", "copy()", "clone()", "Object.copy()", 0));
        js.add(q("What is NaN?", "Not a Node", "Not a Number", "Null and None", "New Array Notation", 1));
        js.add(q("Which method iterates array elements?", "loop()", "forEach()", "iterate()", "each()", 1));
        js.add(q("What does localStorage do?", "Stores session data", "Stores persistent key-value pairs", "Stores cookies", "Connects to database", 1));
        js.add(q("What is a Promise?", "A synchronous function", "An object for async operations", "A class method", "An event listener", 1));
        QUESTION_BANK.put("javascript", js);

        // React Questions
        List<Map<String, Object>> react = new ArrayList<>();
        react.add(q("What is JSX?", "A database", "JavaScript XML syntax extension", "A CSS framework", "A build tool", 1));
        react.add(q("Which hook manages state in functional components?", "useEffect", "useState", "useContext", "useRef", 1));
        react.add(q("What does useEffect do?", "Manages state", "Handles side effects", "Creates components", "Renders UI", 1));
        react.add(q("What is a prop in React?", "Internal state", "Data passed to component", "A hook", "An event", 1));
        react.add(q("Which method renders a React component to DOM?", "React.render()", "ReactDOM.render()", "Component.render()", "React.mount()", 1));
        react.add(q("What is the virtual DOM?", "A browser feature", "An in-memory representation of real DOM", "A CSS engine", "A server component", 1));
        react.add(q("Which hook subscribes to context?", "useContext", "useState", "useReducer", "useRef", 0));
        react.add(q("What does key prop help with?", "Styling", "Efficient list re-rendering", "State management", "Event handling", 1));
        react.add(q("How do you handle form input in React?", "Direct DOM manipulation", "Controlled components with state", "jQuery", "Refs only", 1));
        react.add(q("What does React.memo do?", "Memoizes state", "Prevents unnecessary re-renders", "Creates context", "Manages effects", 1));
        QUESTION_BANK.put("react", react);
    }

    private static Map<String, Object> q(String question, String a, String b, String c, String d, int correct) {
        Map<String, Object> q = new HashMap<>();
        q.put("question", question);
        q.put("options", List.of(a, b, c, d));
        q.put("correct", correct);
        return q;
    }

    // REQ-F-27: Return 10-15 random questions for skill
    @GetMapping("/questions/{skillName}")
    public ResponseEntity<?> getQuestions(@PathVariable String skillName, @RequestParam Long seekerId) {
        String key = skillName.toLowerCase();

        // REQ-F-31: Check 24-hour cooldown for failed attempts
        Optional<SkillTest> lastTest = skillTestRepository
                .findTopBySeekerIdAndSkillNameOrderByTestDateDesc(seekerId, skillName);
        if (lastTest.isPresent()) {
            SkillTest lt = lastTest.get();
            if (!lt.getPassed()) {
                long hoursSince = ChronoUnit.HOURS.between(lt.getTestDate(), LocalDateTime.now());
                if (hoursSince < 24) {
                    long hoursLeft = 24 - hoursSince;
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Please wait " + hoursLeft + " more hour(s) before retaking this test",
                        "cooldownHours", hoursLeft
                    ));
                }
            } else {
                // Already passed — return badge info
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "You have already passed this skill test!",
                    "alreadyPassed", true
                ));
            }
        }

        if (!QUESTION_BANK.containsKey(key))
            return ResponseEntity.badRequest().body(Map.of("error", "No questions available for: " + skillName));

        List<Map<String, Object>> all = new ArrayList<>(QUESTION_BANK.get(key));
        Collections.shuffle(all);
        // REQ-F-27: 10-15 questions
        int count = Math.min(all.size(), 12);
        List<Map<String, Object>> selected = all.subList(0, count);

        // Strip correct answer before sending to frontend
        List<Map<String, Object>> safe = selected.stream().map(q -> {
            Map<String, Object> s = new HashMap<>();
            s.put("question", q.get("question"));
            s.put("options", q.get("options"));
            return s;
        }).collect(Collectors.toList());

        // Store correct answers in session (we'll verify on submit using full question text)
        return ResponseEntity.ok(Map.of(
            "questions", safe,
            "skillName", skillName,
            "totalQuestions", count,
            "timeMinutes", 20
        ));
    }

    // REQ-F-29, F-30: Submit answers, calculate score, mark verified if >= 70
    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@RequestBody Map<String, Object> body) {
        Long seekerId = Long.parseLong(body.get("seekerId").toString());
        String skillName = body.get("skillName").toString();
        @SuppressWarnings("unchecked")
        List<Integer> answers = (List<Integer>) body.get("answers");
        @SuppressWarnings("unchecked")
        List<String> questionTexts = (List<String>) body.get("questions");

        String key = skillName.toLowerCase();
        if (!QUESTION_BANK.containsKey(key))
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid skill"));

        // Match submitted questions to bank and check answers
        Map<String, Integer> bankMap = QUESTION_BANK.get(key).stream()
                .collect(Collectors.toMap(q -> q.get("question").toString(), q -> (Integer) q.get("correct")));

        int correct = 0;
        int total = questionTexts.size();
        for (int i = 0; i < total; i++) {
            String qText = questionTexts.get(i);
            Integer correctIdx = bankMap.get(qText);
            if (correctIdx != null && i < answers.size() && answers.get(i).equals(correctIdx)) {
                correct++;
            }
        }

        int score = (int) Math.round((double) correct / total * 100);
        boolean passed = score >= 70;

        // REQ-F-32: Save test history
        SkillTest test = new SkillTest();
        test.setSeekerId(seekerId);
        test.setSkillName(skillName);
        test.setScore(score);
        test.setPassed(passed);
        skillTestRepository.save(test);

        // REQ-F-30: If passed, add skill to user profile
        if (passed) {
            userRepository.findById(seekerId).ifPresent(user -> {
                String existing = user.getSkills() != null ? user.getSkills() : "";
                List<String> skillList = Arrays.stream(existing.split(","))
                        .map(String::trim).filter(s -> !s.isEmpty())
                        .collect(Collectors.toCollection(ArrayList::new));
                if (!skillList.stream().anyMatch(s -> s.equalsIgnoreCase(skillName))) {
                    skillList.add(skillName);
                    user.setSkills(String.join(", ", skillList));
                    userRepository.save(user);
                }
            });
        }

        return ResponseEntity.ok(Map.of(
            "score", score,
            "correct", correct,
            "total", total,
            "passed", passed,
            "skillName", skillName,
            "message", passed
                ? "Congratulations! You passed! " + skillName + " has been added to your profile."
                : "You scored " + score + "%. You need 70% to pass. Try again after 24 hours."
        ));
    }

    // Get all test results for a user
    @GetMapping("/tests/{seekerId}")
    public ResponseEntity<?> getUserTests(@PathVariable Long seekerId) {
        return ResponseEntity.ok(skillTestRepository.findBySeekerId(seekerId));
    }

    // List of available skill categories
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(List.of("Java", "Python", "SQL", "Spring Boot", "JavaScript", "React"));
    }
}
