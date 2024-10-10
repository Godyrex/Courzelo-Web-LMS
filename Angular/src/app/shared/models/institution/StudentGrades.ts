export interface StudentGrades {
    studentEmail: string;
    image: string;
    grades: { [key: string]: number };
}