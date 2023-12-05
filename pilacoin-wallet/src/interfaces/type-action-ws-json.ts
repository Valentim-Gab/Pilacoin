export interface TypeActionWsJson {
  message: string
  type: TypeAction
  timestamp?: number
}

export enum TypeAction {
  VALIDATION_PILACOIN = "VALIDATION_PILACOIN",
  VALIDATION_BLOCK = "VALIDATION_BLOCK",
  MINER_BLOCK = "MINER_BLOCK",
  MINER_PILACOIN = "MINER_PILACOIN",
}