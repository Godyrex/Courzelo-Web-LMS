export interface ModuleRequest {
    name: string;
    description: string;
    skills: string[];
    semester: string;
    scoreToPass: number;
    duration: string;
    isFinished: boolean;
    credit: number;
    program: string;
    moduleParts: Map<string, number>;
}
