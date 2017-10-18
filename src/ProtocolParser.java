/*Prof Alcides 2017*/

//package pt.uc.dei.sd.ibei.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProtocolParser {
    public static HashMap<String, String> parse(String line) {
		HashMap<String, String> g = new HashMap<>();
		Arrays.stream(line.split(",")).map(s -> s.split(":")).forEach( i -> g.put(i[0].trim(), i[1].trim()) );
		return g;
	}
    
    public static List<HashMap<String, String>> getList(HashMap<String, String> map, String field) {
    	if (!map.containsKey(field + "_count")) {
    		throw new NoSuchElementException();
    	}
    	int count = Integer.parseInt(map.get(field + "_count"));
    	return IntStream.range(0, count).mapToObj((int i) -> {
    		HashMap<String, String> im = new HashMap<>();
    		String prefix = field + "_" + i;
			map.keySet().stream().filter((t) -> t.startsWith(prefix)).forEach((k) -> {
				im.put(k.substring(prefix.length()+1), map.get(k));
			});
			return im;
    	}).collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
    	String a = "type : search_auction , items_count : 2, items_0_id : 101, items_0_code : 9780451524935, items_0_title : 1984, items_1_id : 103, items_1_code : 9780451524935, items_1_title : 1984 usado";
    	HashMap<String, String> m = ProtocolParser.parse(a);
    	
    	assert(m.get("type").equals("search_auction"));
    	assert(ProtocolParser.getList(m, "items").size() > 0);
    	assert(ProtocolParser.getList(m, "items").get(0).get("id").equals("101"));
    	assert(ProtocolParser.getList(m, "items").get(1).get("code").equals("9780451524935"));
    	
    	for (HashMap<String, String> element : ProtocolParser.getList(m, "items")) {
    		assert(Integer.parseInt(element.get("id")) > 0);
    	}
    	
    }
}