import os
from faker import Faker


# The HL7 fields that are checked by the deduplication algorithm for patient are as follows:
#    1. MSH 4.2 (SendingApplication.UniversalID) <- how do you do this?
#    2. OBR 3.1 (FillerOrderNumber.entity identifier) <- 01D0641691?
#    3. OBR 4.1 (Universal service ID.Identifier)
#    4. OBR 4.4 (Universal service ID.Alternate Identifier)
#    5. OBR 7 (Observation Date/Time) <- 2.3.1
#    6. SPM 17 (Specimen Collection Date/Time) <- 2.5.1
#    7. PID.2.1 - Id Number
#    8. PID.2.5 - Identifier Type Code
#    9. PID.4.4 - Assigning Authority
#    10. PID.5.1 - Family Name
#    11. PID.5.2 - Given Name
#    12. PID.7 - Date/Time of Birth
#    13. PID.8 - Administrative Sex


def generate_unique_patient_messages(num_messages, output_folder):
    fake = Faker()
    os.makedirs(output_folder, exist_ok=True)

    for _ in range(num_messages):
        id_number = fake.random_int(min=10000, max=99999)
        patient_id = fake.random_int(min=10000, max=99999)
        person_number = fake.random_int(min=10000, max=99999)
        family_name = fake.last_name()
        given_name = fake.first_name()
        name1= fake.first_name()
        name1last= fake.last_name()
        name2= fake.first_name()
        name2last =fake.last_name()
        date_of_birth = fake.date_of_birth(minimum_age=18, maximum_age=90).strftime('%Y%m%d')
        date = fake.date()
        administrative_sex = fake.random_element(elements=('M', 'F', 'Other'))
        ssn = fake.ssn()
        address = fake.address()
        state = fake.state()
        city = fake.city()
        zipcode = fake.zipcode()
        assigning_authority = fake.company()
        assigning_authority_id = fake.random_int(min=10000, max=99999)
        sending_app_id = fake.random_int(min=10000, max=99999)
        filler_order_entity_id = fake.random_int(min=0, max=999)
        apt_no = fake.random_int(min= 100, max= 10000)
        alternate_identifier = fake.random_int(min= 999, max= 99999)

        fake_message = (
            f"MSH|^~\\&|LABCORP-CORP^OID^ISO|LABCORP^20D0649525^CLIA|ALDOH^OID^ISO|AL^OID^ISO|200604040100||ORU^R01^ORU_R01|20120509010020114_251.2|D|2.5.1|||NE|NE|USA||||V251_IG_LB_LABRPTPH_R1_INFORM_2010FEB^^2.16.840.1.114222.4.3.2.5.2.5^ISO\r"
            f"SFT|Mirth Corp.|2.0|Mirth Connect|789654||20110101\r"
            f"PID|1||05540205114^^^LABC&OID&ISO^PN~17485458372^^^AssignAuth&OID&ISO^PI^NE CLINIC&24D1040593&CLIA||{family_name}^{given_name}|MOTHER^PATERSON|196011092025|F||W^WHITE^HL70005|5000 Staples Dr^APT100^SOMECITY^ME^30342^^H\r"
            f"NK1|1|TESTNOK114B^FIRSTNOK1^X^JR^DR^MD|FTH|12 MAIN STREET^SUITE 16^COLUMBIA^SC^30329^USA^^^RICHLAND|^^^^^803^5551212^123\r"
            f"ORC|RE||20120601114^LABCORP^20D0649525^CLIA||||||||||||||||||COOSA VALLEY MEDICAL CENTER|315 WEST HICKORY ST.^SUITE 100^SYLACAUGA^AL^35150^USA^^^RICHLAND|^^^^^256^2495780^123|380 WEST HILL ST.^^SYLACAUGA^AL^35150^USA^^^RICHLAND\r"
            f"OBR|1||20120601114^LABCORP^20D0649525^CLIA|{patient_id}{family_name}-9^ORGANISM COUNT^LN^080186^CULTURE^L|||200603241655|200603241655||342384^JONES^SUSAN||||||46466^BRENTNALL^GERRY^LEE^SR^DR^MD|^^^^^256^2495780|||||200604040139|||F|||46214^MATHIS^GERRY^LEE^SR^DR^MD~44582^JONES^THOMAS^LEE^III^DR^MD~46111^MARTIN^JERRY^L^JR^DR^MD|||12365-4^TOTALLY CRAZY^I9|22582&JONES&TOM&L&JR&DR&MD|22582&MOORE&THOMAS&E&III&DR&MD|44&JONES&SAM&A&JR&MR&MT|82&JONES&THOMASINA&LEE ANN&II&MS&RA\r"
            f"OBX|1|SN|^TITER^LN^080133|1|100^1^:^1|mL|10-100|H|||F||||20D0649525^LABCORP BIRMINGHAM^CLIA||||20060401||||Lab1^L^^^^CLIA&2.16.840.1.114222.4.3.2.5.2.100&ISO^^^^1234|1234 Cornell Park Dr^^Blue Ash^OH^45241|9876543^JONES^BOB^F^^^MD\r"
            f"SPM|1|^56789&LABCORP&20D0649525&CLIA|^20120601114&LABCORP&20D0649525&CLIA|^^^BLD^BLOOD^L||||LA^^HL70070||||||SOURCE NOT SPECIFIED|||200603241655^200603241655|200603250137\r"
        )

        # Create a separate text file for each message
        file_name = os.path.join(output_folder, f"{given_name}_{family_name}.txt")
        with open(file_name, 'w') as text_file:
            text_file.write(fake_message)

if __name__ == "__main__":
    generate_unique_patient_messages(1000, "/Users/DucNguyen/Downloads/python_code_for_di/data")