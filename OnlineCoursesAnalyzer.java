import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


/**
 * This is just a demo for you, please run it on JDK17.
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses=new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br=new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line=br.readLine()) != null) {
                String[] info=line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]),
                        Integer.parseInt(info[8]), Integer.parseInt(info[9]),
                        Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                        Double.parseDouble(info[14]), Double.parseDouble(info[15]),
                        Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                        Double.parseDouble(info[20]), Double.parseDouble(info[21]),
                        Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> ans=courses.stream()
                .collect(Collectors.groupingBy(Course::getInstitution,
                        Collectors.summingInt(Course::getParticipants)));
        return new TreeMap<>(ans);
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> ans=courses.stream().collect(Collectors.groupingBy(Course::q2,
                Collectors.summingInt(Course::getParticipants)));
        Map<String, Integer> answer = new TreeMap<>(ans);
        answer = answer.entrySet().stream().sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return answer;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> ans = new HashMap<>();
        for (Course course : courses) {
            String[] in = course.instructors.split(", ");
            if (in.length > 1) {
                for (int i = 0; i < in.length; i++) {
                    if (ans.containsKey(in[i])) {
                        if (!ans.get(in[i]).get(1).contains(course.title)) {
                            ans.get(in[i]).get(1).add(course.title);
                        }
                        Collections.sort(ans.get(in[i]).get(1));
                    } else {
                        List<String> a = new ArrayList<>();
                        List<String> b = new ArrayList<>();
                        b.add(course.title);
                        List<List<String>> s = new ArrayList<>();
                        s.add(a);
                        s.add(b);
                        ans.put(in[i], s);
                    }
                }
            } else {
                if (ans.containsKey(in[0])) {
                    if (!ans.get(in[0]).get(0).contains(course.title)) {
                        ans.get(in[0]).get(0).add(course.title);
                    }
                    Collections.sort(ans.get(in[0]).get(0));
                } else {
                    List<String> a = new ArrayList<>();
                    List<String> b = new ArrayList<>();
                    a.add(course.title);
                    List<List<String>> s = new ArrayList<>();
                    s.add(a);
                    s.add(b);
                    ans.put(in[0], s);
                }
            }
        }
        System.out.println(ans.size());
        return ans;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        List<String> ans = new ArrayList<>();
        Map<String, Double> a = new HashMap<>();
        if (by.equals("hours")) {
            for (Course course : courses) {
                if (a.containsKey(course.title)) {
                    double b = a.get(course.title);
                    if (course.totalHours > b) {
                        a.replace(course.title, b, course.totalHours);
                    }
                } else {
                    a.put(course.title, course.totalHours);
                }
            }
        } else {
            for (Course course : courses) {
                if (a.containsKey(course.title)) {
                    if (course.participants > a.get(course.title)) {
                        a.replace(course.title, a.get(course.title), (double) course.participants);
                    }
                } else {
                    a.put(course.title, (double) course.participants);
                }
            }
        }
        int i = 0;
        a = a.entrySet().stream().sorted((o1, o2) -> (int) (o2.getValue() - o1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (String key : a.keySet()) {
            if (i < topK) {
                ans.add(key);
            } else {
                break;
            }
            i++;
        }
        return ans;
    }

    //5
    public List<String> searchCourses(String courseSubject,
                                      double percentAudited, double totalCourseHours) {
        List<String> ans = new ArrayList<>();
        for (Course course : courses) {
            if (course.subject.toUpperCase(Locale.ROOT).contains(courseSubject.toUpperCase(Locale.ROOT))
                    && course.percentAudited >= percentAudited
                    && course.totalHours <= totalCourseHours) {
                if (!ans.contains(course.title)) {
                    ans.add(course.title);
                }
            }
        }
        Collections.sort(ans);
        return ans;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        List<String> ans = new ArrayList<>();
        Map<String, Double> avg_age = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber,
                        Collectors.averagingDouble(Course::getMedianAge)));
        Map<String, Double> avg_male = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber,
                        Collectors.averagingDouble(Course::getPercentMale)));
        Map<String, Double> avg_degree = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber,
                        Collectors.averagingDouble(Course::getPercentDegree)));
        Map<String, Double> S = new HashMap<>();
        for (String key : avg_age.keySet()) {
            double s = (age - avg_age.get(key)) * (age - avg_age.get(key))
                    + (100 * gender - avg_male.get(key)) * (100 * gender - avg_male.get(key))
                    + (100 * isBachelorOrHigher - avg_degree.get(key))
                    * (100 * isBachelorOrHigher - avg_degree.get(key));
            S.put(key, s);
        }
        S = S.entrySet().stream().sorted((o1, o2) -> Double.compare(o1.getValue(), o2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Map<String, String> name = new HashMap<>();
        Map<String, Date> date = new HashMap<>();
        for (Course course : courses) {
            if (!date.containsKey(course.number)) {
                date.put(course.number, course.launchDate);
                name.put(course.number, course.title);
            } else {
                if (course.launchDate.after(date.get(course.number))) {
                    date.replace(course.number, date.get(course.number), course.launchDate);
                    name.replace(course.number, name.get(course.number), course.title);
                }
            }
        }
        Map<String, Double> answer = new TreeMap<>();
        for (String key : S.keySet()) {
            if (!answer.containsKey(name.get(key))) {
                answer.put(name.get(key), S.get(key));
            } else {
                if (S.get(key) > answer.get(name.get(key))) {
                    answer.replace(name.get(key), answer.get(key), S.get(key));
                }
            }
        }
        answer = answer.entrySet().stream().sorted(
                        (o1, o2) -> Double.compare(o1.getValue(), o2.getValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue)
                                -> oldValue, LinkedHashMap::new));
        int i = 0;
        System.out.println(S.size());
        for (String key : answer.keySet()) {
            if (i == 10) {
                break;
            }
            ans.add(key);
            i++;
        }
        return ans;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }

        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getInstitution() {
        return institution;
    }

    public int getParticipants() {
        return participants;
    }

    public String q2() {
        return institution + '-' + subject;
    }

    public String getTitle() {
        return title;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public double getPercentMale() {
        return percentMale;
    }

    public double getPercentDegree() {
        return percentDegree;
    }

    public String getNumber() {
        return number;
    }

    public Date getLaunchDate() {
        return launchDate;
    }
}
