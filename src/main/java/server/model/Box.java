package server.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Box {

    @SerializedName("per")
    private byte percentage;
    private String name;

    private transient Champion owner;
}
