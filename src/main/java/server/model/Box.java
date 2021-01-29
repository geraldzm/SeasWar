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


    // true if owner die
    public boolean setPercentage(int percentage) {
        this.percentage = (byte) (Math.max(percentage, 0));

        if(this.percentage == 0){ // dead
            return owner.boxDied();
        }
        return false;
    }
}
