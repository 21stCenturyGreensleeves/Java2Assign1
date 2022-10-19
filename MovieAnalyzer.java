import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MovieAnalyzer {
  String path;
    public MovieAnalyzer(String dataset_path) throws IOException {
        this.path = dataset_path;
    }
    //1======================================================================================================================
    public Map<Integer, Integer> getMovieCountByYear() throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        br.readLine();
        Map<Integer, Integer> tar = new TreeMap<Integer, Integer>(
                new Comparator<Integer>(){
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return  (o2-o1);
                    }
                });

        while((line = br.readLine()) != null){
            String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            Integer year = Integer.parseInt(arr[2]);
            if(tar.containsKey(year)){
                tar.put(year,tar.get(year)+1);
            }else{
                tar.put(year,1);
            }
        }
//        System.out.println(tar+"\n");
        return tar;
    }
    //2======================================================================================================================
    public Map<String, Integer> getMovieCountByGenre() throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        br.readLine();
        HashMap<String, Integer> hashmap = new HashMap<>();
        while((line = br.readLine()) != null){
            String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            String[] tmp = arr[5].replace("\"","").split(", ");
            for(String i : tmp){
                if(!hashmap.containsKey(i)){
                    hashmap.put(i,1);
                }else{
                    hashmap.put(i,hashmap.get(i)+1);
                }
            }
        }
        Map<String, Integer> sortedMap = hashmap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2-o1;
                    }
                }))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
        return sortedMap;
    }
    //3======================================================================================================================
    public Map<List<String>, Integer> getCoStarCount() throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        br.readLine();
        Map<List<String>,Integer> hashMap = new HashMap<>();
        while((line = br.readLine()) != null){ //10 11 12 13
            String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            List<String> list1 = new ArrayList<>();
            list1.add(arr[10]);list1.add(arr[11]);
            Collections.sort(list1);
            List<String> list2 = new ArrayList<>();
            list2.add(arr[10]);list2.add(arr[12]);
            Collections.sort(list2);
            List<String> list3 = new ArrayList<>();
            list3.add(arr[10]);list3.add(arr[13]);
            Collections.sort(list3);
            List<String> list4 = new ArrayList<>();
            list4.add(arr[11]);list4.add(arr[12]);
            Collections.sort(list4);
            List<String> list5 = new ArrayList<>();
            list5.add(arr[11]);list5.add(arr[13]);
            Collections.sort(list5);
            List<String> list6 = new ArrayList<>();
            list6.add(arr[12]);list6.add(arr[13]);
            Collections.sort(list6);
            List<List<String>> list = new ArrayList<>();
            list.add(list1);list.add(list2);list.add(list3);list.add(list4);list.add(list5);list.add(list6);
            for(int i = 0;i<list.size();i++){
                if(!hashMap.containsKey(list.get(i))){
                    hashMap.put(list.get(i),1);
                }else{
                    hashMap.put(list.get(i),hashMap.get(list.get(i))+1);
                }
            }
        }Map<List<String>, Integer> sortedMap = hashMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2-o1;
                    }
                }))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
        return sortedMap;
    }
    //4======================================================================================================================
    public List<String> getTopMovies(int top_k, String by) throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br1 = new BufferedReader(isr);
        String line;
        br1.readLine();

        IdentityHashMap<Movie, Integer> runTimeMap = new IdentityHashMap<>();
        Map<Movie, Integer> overviewMap = new HashMap<>();
        while((line = br1.readLine())!=null){
            String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if(arr[7].charAt(0)=='\"'){
                arr[7] = arr[7].substring(1,arr[7].length()-1);
            }
            Integer lengthOfOverview = arr[7].length();
            Integer min = Integer.parseInt(arr[4].replace(" min",""));
            arr[1] = arr[1].replace("\"","");
            Movie movie = new Movie(arr[1]);
            runTimeMap.put(movie,min);
            overviewMap.put(movie,lengthOfOverview);
        }

        Map<Movie, Integer> sortedRunTimeMap = runTimeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(new Comparator<Movie>() {
                    @Override
                    public int compare(Movie o1, Movie o2) {
                        return o1.Title.compareTo(o2.Title);
                    }
                }))
                .sorted(Map.Entry.comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2-o1;
                    }
                }))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
        Map<Movie, Integer> sortedOverviewMap = overviewMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(new Comparator<Movie>() {
                    @Override
                    public int compare(Movie o1, Movie o2) {
                        return o1.Title.compareTo(o2.Title);
                    }
                }))
                .sorted(Map.Entry.comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2-o1;
                    }
                }))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
        List<String> tar = new ArrayList<>();
        if(by.equals("runtime")){
            int count = 0;
            for (Map.Entry<Movie, Integer> entry : sortedRunTimeMap.entrySet() ) {
                if(count >= top_k){
                    break;
                }else{
                    String mapKey = entry.getKey().Title;
                    tar.add(mapKey);
                    count++;
                }
            }
        }else{
            int count = 0;
            for (Map.Entry<Movie, Integer> entry : sortedOverviewMap.entrySet() ) {
                if(count >= top_k){
                    break;
                }else{
                    String mapKey = entry.getKey().Title;
                    tar.add(mapKey);
                    count++;
                }
            }
        }
        return tar;
    }
    //5======================================================================================================================
    public List<String> getTopStars(int top_k, String by) throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        br.readLine();
        List<String> tar = new ArrayList<>();

        Map<String, Double> ratingMap = new TreeMap<>(Comparator.naturalOrder());
        Map<String,Long> grossMap = new HashMap<>();
        Map<String,Double> timeMap = new HashMap<>();
        Map<String,Double>  grossTimeMap = new HashMap<>();
        while((line = br.readLine()) != null){

            String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            long gross;
            if(!Objects.equals(arr[15], "")){
                arr[15] = arr[15].replace(",","");
                arr[15] = arr[15].replace("\"","");
                gross = Long.parseLong(arr[15]);
            } else {
                gross = 0;
            }
            List<String> stars = new ArrayList<>();
            stars.add(arr[10]);
            stars.add(arr[11]);
            stars.add(arr[12]);
            stars.add(arr[13]);
            float rating = Float.parseFloat(arr[6]);
            for (String star : stars) {
                if (!ratingMap.containsKey(star)) {
                    ratingMap.put(star, (double) rating);
                    timeMap.put(star,1.0);
                } else {
                    ratingMap.replace(star, ratingMap.get(star) + rating);
                    timeMap.put(star,timeMap.get(star)+1);

                }
            }for(String star : stars){
                if(!grossMap.containsKey(star)){
                    if(!Objects.equals(arr[15], "")){
                        grossMap.put(star, gross);
                        grossTimeMap.put(star,1.0);
                    }
                }else{
                    if(!Objects.equals(arr[15], "")){
                        grossMap.put(star, grossMap.get(star) + gross);
                        grossTimeMap.put(star,grossTimeMap.get(star)+1);
                    }
                }
            }
        }
        for (Map.Entry<String, Double> entry : ratingMap.entrySet() ) {
            Double ratingSum = entry.getValue();
            String actor = entry.getKey();
            ratingMap.put(actor,ratingSum/timeMap.get(actor));
        }
        for (Map.Entry<String, Long> entry : grossMap.entrySet() ) {
            Long grossSum = entry.getValue();
            String actor = entry.getKey();
            grossMap.put(actor, (long) (grossSum/grossTimeMap.get(actor)));
        }

        Map<String, Double> sortedRatingMap = ratingMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.replace(" ","").compareTo(o2.replace(" ",""));
                    }
                }))
                .sorted(Map.Entry.comparingByValue(new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        return o2.compareTo(o1);
                    }
                }))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );

        Map<String, Long> sortedGrossMap = grossMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(new Comparator<Long>() {
                    @Override
                    public int compare(Long o1, Long o2) {
                        return (int) (o2-o1);
                    }
                }))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
        int count = 0;
        if(by.equals("rating")){
            for (Map.Entry<String, Double> entry : sortedRatingMap.entrySet() ) {
                if(count >= top_k){
                    break;
                }else{
                    String mapKey = entry.getKey();
                    tar.add(mapKey);
                    count++;
                }
            }
        }else{
            for (Map.Entry<String, Long> entry : sortedGrossMap.entrySet() ) {
                if(count >= top_k){
                    break;
                }else{
                    String mapKey = entry.getKey();
                    tar.add(mapKey);
                    count++;
                }
            }
        }
        return tar;
    }
    //6======================================================================================================================
    public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        br.readLine();
        List<String> list = new ArrayList<>();
        while((line = br.readLine())!=null){
            String[] arr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            int time = Integer.parseInt(arr[4].replace(" min",""));
            BigDecimal b = new BigDecimal(String.valueOf(min_rating));
            double tmp = b.doubleValue();
            double rate = Double.parseDouble(arr[6]);
            List<String> Gnr = List.of(arr[5].replace("\"", "").split(", "));
            if(time <= max_runtime && rate >= tmp && Gnr.contains(genre)){
                list.add(arr[1].replace("\"",""));
            }
            Collections.sort(list);
        }
        return list;
    }
}
class Movie{
    String Title;
    String Released_Year;
    String Certificate;
    String Runtime;
    String Genre;
    String IMDB_Rating;
    String Overview;
    String Meta_Score;
    String Director;
    String[] stars;
    String No_of_Votes;
    String Gross;
    public Movie(String Title){
        this.Title = Title;
    }

}