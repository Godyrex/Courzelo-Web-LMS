import { TicketType } from "./TicketType";

export interface Ticket {
  id : string,	
  sujet:string,	
  details:string,	
  dateCreation:Date,
  type:TicketType,	
  status:string
}