package dc.codecademy.miniproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pair<K, V> {
    private K first;
    private V second;

    public K first() {
        return first;
    }

    public V second() {
        return second;
    }

}
