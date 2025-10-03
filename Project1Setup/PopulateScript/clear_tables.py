from sqlalchemy import create_engine, text

def main():
    engine = create_engine("postgresql://postgres:password@localhost:5432/pds_proj_1")
    
    with engine.connect() as conn:
        # Delete all data from tables
        print("Clearing med_data table...")
        conn.execute(text("DELETE FROM med_data"))
        
        print("Clearing med_data_deidentified table...")
        conn.execute(text("DELETE FROM med_data_deidentified"))
        
        print("Clearing work_data table...")
        conn.execute(text("DELETE FROM work_data"))
        
        conn.commit()
        print("All tables cleared successfully!")

if __name__ == '__main__':
    main()
