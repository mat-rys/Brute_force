import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UpdatePC3Env {
    public static void main(String[] args) throws Exception {
        File file = new File("src/main/resources/benchmark_results_pc3.json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(file, Map.class);

        Map<String, String> env = new HashMap<>();
        env.put("os", "Windows 10 10.0 (amd64)");
        env.put("cpu", "AMD Ryzen 5 2600 Six-Core Processor");
        env.put("gpu", "NVIDIA GeForce GTX 1060 6GB");
        env.put("cores", "12");
        env.put("ramPhysical", "16334 MB");
        env.put("java", "Oracle Corporation JDK 19.0.2");
        env.put("timestamp", "2026-05-15 11:52:16");

        data.put("environment", env);
        mapper.writeValue(file, data);
        System.out.println("PC3 Environment updated successfully.");
    }
}
