import {ModuleResponse} from './ModuleResponse';

export interface PaginatedModulesResponse {
    modules: ModuleResponse[];
    currentPage: number;
    totalPages: number;
    totalItems: number;
    itemsPerPage: number;
}
