export interface GradeResponse {
    id: string;
    name: string;
    moduleID: string;
    moduleName: string;
    scoreToPass: number;
    groupID: string;
    institutionID: string;
    studentEmail: string;
    grade: number;
    average: number;
    valid: boolean;
}
