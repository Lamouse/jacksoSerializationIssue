import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class JacksonTest {

    @Test
    public void simpleTypeTest() throws IOException {
        CustomMap<String> map1 = new CustomMap<>();
        String value1 = "item1";

        final byte[] serialized = map1.serialize(value1);
        System.out.println("Serialized : " + new String(serialized));
        map1.deserialize(serialized);
    }

    @Test
    public void collectionTypeTest() throws IOException {
        CustomMap<ArrayList<String>> map1 = new CustomMap<>();
        ArrayList<String> value1 = new ArrayList<>(Arrays.asList("item1", "item2"));

        final byte[] serialized = map1.serialize(value1);
        System.out.println("Serialized : " + new String(serialized));
        map1.deserialize(serialized);
    }

    @Test
    public void simpleTypeTestDeserializeWithType() throws IOException {
        CustomMap<String> map1 = new CustomMap<>();

        String value1 = "item1";
        final byte[] serialized = map1.serialize(value1);

        System.out.println("Serialized : " + new String(serialized));
        map1.deserialize(serialized, new TypeReference<BaseValue<String>>() { });
    }

    @Test
    public void collectionTypeTestDeserializeWithType() throws IOException {
        CustomMap<ArrayList<String>> map1 = new CustomMap<>();
        ArrayList<String> value1 = new ArrayList<>(Arrays.asList("item1", "item2"));

        final byte[] serialized = map1.serialize(value1);
        System.out.println("Serialized : " + new String(serialized));
        map1.deserialize(serialized, new TypeReference<BaseValue<ArrayList<String>>>() { });
    }
}

@JsonIgnoreProperties
class BaseValue<V extends Serializable> implements Serializable {

    private V data;

    @JsonCreator
    BaseValue(@JsonProperty("data") V data) {
        this.data = data;
    }

    @JsonProperty("data")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = Serializable.class)
    public V getData() {
        return data;
    }
}

class CustomMap<V extends Serializable> {

    byte[] serialize(V data) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(new BaseValue<>(data)).getBytes();
    }

    void deserialize(byte[] data, TypeReference<BaseValue<V>> valueTypeRef) throws IOException {
        getObjectMapper().readValue(data, valueTypeRef);
    }

    void deserialize(byte[] data) throws IOException {
        getObjectMapper().readValue(data, new TypeReference<BaseValue<V>>() {});
    }

    private ObjectMapper  getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        return mapper;
    }
}
