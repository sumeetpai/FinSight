export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export interface Database {
  public: {
    Tables: {
      stocks: {
        Row: {
          id: string
          symbol: string
          name: string
          current_price: number
          previous_close: number
          market_cap: number
          created_at: string
          updated_at: string
        }
        Insert: {
          id?: string
          symbol: string
          name: string
          current_price?: number
          previous_close?: number
          market_cap?: number
          created_at?: string
          updated_at?: string
        }
        Update: {
          id?: string
          symbol?: string
          name?: string
          current_price?: number
          previous_close?: number
          market_cap?: number
          created_at?: string
          updated_at?: string
        }
      }
      portfolios: {
        Row: {
          id: string
          user_id: string
          name: string
          description: string
          created_at: string
          updated_at: string
        }
        Insert: {
          id?: string
          user_id: string
          name: string
          description?: string
          created_at?: string
          updated_at?: string
        }
        Update: {
          id?: string
          user_id?: string
          name?: string
          description?: string
          created_at?: string
          updated_at?: string
        }
      }
      portfolio_holdings: {
        Row: {
          id: string
          portfolio_id: string
          stock_id: string
          shares: number
          average_cost: number
          created_at: string
          updated_at: string
        }
        Insert: {
          id?: string
          portfolio_id: string
          stock_id: string
          shares?: number
          average_cost?: number
          created_at?: string
          updated_at?: string
        }
        Update: {
          id?: string
          portfolio_id?: string
          stock_id?: string
          shares?: number
          average_cost?: number
          created_at?: string
          updated_at?: string
        }
      }
      transactions: {
        Row: {
          id: string
          portfolio_id: string
          stock_id: string
          transaction_type: 'BUY' | 'SELL'
          shares: number
          price_per_share: number
          total_amount: number
          transaction_date: string
          created_at: string
        }
        Insert: {
          id?: string
          portfolio_id: string
          stock_id: string
          transaction_type: 'BUY' | 'SELL'
          shares: number
          price_per_share: number
          total_amount: number
          transaction_date?: string
          created_at?: string
        }
        Update: {
          id?: string
          portfolio_id?: string
          stock_id?: string
          transaction_type?: 'BUY' | 'SELL'
          shares?: number
          price_per_share?: number
          total_amount?: number
          transaction_date?: string
          created_at?: string
        }
      }
    }
  }
}
