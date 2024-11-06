package org.example.courzelo.dto.responses.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedModulesResponse {
    private List<ModuleResponse> modules;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int itemsPerPage;
}
