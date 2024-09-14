import {Question} from './Question';
import {status} from './status';



export class Quiz {
    id?: string;
    userEmail: string;
    title: string;
    description: string;
    questions: Question[] = [];
    duration: number;
    course: string;
    showSummary ? = false;
    finalScore ? = 0;
    maxScore ? = 0;
    quizStarted = false;
    quizEnded = false;
    timeRemaining: number;
}
export class QuizSubmission {
    quizID: string;
    answers: { questionID: string, answer: string }[] = [];
}
export class QuizSubmissionResult {
    quizID: string;
    score: number;
    maxScore: number;
    passed: boolean;
    status: status;
    submittedAt: Date;
    Answers: { questionID: string, answer: string }[] = [];
}


