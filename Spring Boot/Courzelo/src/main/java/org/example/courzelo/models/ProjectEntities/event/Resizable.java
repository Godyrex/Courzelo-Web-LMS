package org.example.courzelo.models.ProjectEntities.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Resizable")
public  class Resizable {
    private boolean beforeStart;
    private boolean afterEnd;
}
