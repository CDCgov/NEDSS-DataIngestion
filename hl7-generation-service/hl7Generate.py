import random
from faker import Faker
import json
#from dictionary.py import PIDvalueGenerate


# ---------- REQUIREMENTS ---------------
# Generate a message with all segments and fields. pid, nk1, msh, obr, orc, obx
# Generate a message excluding specific segments.
# Generate a message excluding specific fields.
# by disease types
# make it configurable with at least 1000 
# Should be updates and not always net new **------- check with Selva
# lims


class HL7v2_5_1_ORU:
    def __init__(self, first, last, option):
        self.first = first
        self.last = last
       # if self.option = 'allsegments':
        #    HL7v2_5_1_ORU.allsegments()
        #if self.option = 'invalid':
           # HL7v2_5_1_ORU.invalid()
        #else print('enter a correct option (allsegment, invalid, update, etc.)')
    
    def MSH():
        # Message Header. This segment is a mandatory part of an ORU message, 
        # and contains information about the message sender and receiver, 
        # the date and time that the message was created. 
        # This segment is required.
        msh1 = msh1 # MSH.1 - Field Separator -- R
        msh2 = msh2 # MSH.2 - Encoding Characters -- R
        msh3_1 = msh3_1 # MSH.3.1 - Namespace Id ---- Sending Application
        msh3_2 = msh3_2 # MSH.3.2 - Universal Id
        msh3_3 = msh3_3 # MSH.3.3 - Universal Id Type
        msh4_1 = msh4_1 # MSH.4.1 - Namespace Id ---- Sending Facility
        msh4_2 = msh4_1 # MSH.4.2 - Universal Id
        msh4_3 = msh4_3 # MSH.4.3 - Universal Id Type
        msh5_1 = msh5_1 # MSH.5.1 - Namespace Id ---- Recieving Application
        msh5_2 = msh5_2 # MSH.5.2 - Universal Id
        msh5_3 = msh5_3 # MSH.5.3 - Universal Id Type
        msh6_1 = msh6_1 # MSH.6.1 - Namespace Id ---- Recieving Facility 
        msh6_2 = msh6_2 # MSH.6.2 - Universal Id
        msh6_3 = msh6_3 # MSH.6.3 - Universal Id Type
        msh7_1 = msh7_1 # MSH.7.1 - Time ---- R
        msh7_2 = msh7_2 # MSH.7.2 - Degree Of Precision
        msh8 = msh8 # MSH.8 - Security 
        msh9_1 = msh9_1 # MSH.9.1 - Message Code ---- R
        msh9_2 = msh9_2 # MSH.9.2 - Trigger Event ---- R
        msh9_3 = msh9_3 # MSH.9.3 - Message Structure ---- R
        msh10 = msh10 # MSH.10 - Message Control ID ---- R
        msh11_1 = msh11_1 # MSH.11.1 - Processing Id ---- R
        msh11_2 = msh11_2 # MSH.11.2 - Processing Mode ---- R
        msh12_1 = msh12_1 # MSH.12.1 - Version Id
        msh12_2 = msh12_2 # MSH.12.2 - Internationalization Code
        msh12_3 = msh12_3 # MSH.12.3 - International Version Id
        msh13 = msh13 # MSH.13 - Sequence Number
        msh14 = msh14 # MSH.14 - Continuation Pointer
        msh15 = msh15 # MSH.15 - Accept Acknowledgment Type
        msh16 = msh16 # MSH.16 - Application Acknowledgment Type
        msh17 = msh17 # MSH.17 - Country Code
        msh18 = msh18 # MSH.18 - Character Set
        msh19_1 = msh19_1 # MSH.19.1 - Identifier ----- Principal Language Of Message
        msh19_2 = msh19_2 # MSH.19.2 - Text
        msh19_3 = msh19_3 # MSH.19.3 - Name Of Coding System
        msh19_4 = msh19_4 # MSH.19.4 - Alternate Identifier
        msh19_5 = msh19_5 # MSH.19.5 - Alternate Text
        msh19_6 = msh19_6 # MSH.19.6 - Name Of Alternate Coding System
        msh21_1 = msh21_1 # MSH.21.1 - Entity Identifier ----- Message Profile Identifier
        msh21_2 = msh21_2 # MSH.21.2 - Namespace Id
        msh21_3 = msh21_3 # MSH.21.3 - Universal Id
        msh21_4 = msh21_4 # MSH.21.4 - Universal Id Type
    def SFT(self):
        pass
    def PID():
        fake = Faker()
        # generating name
        firstname = fake.first_name()
        lastname = fake.last_name()
        fullname = firstname + " " + lastname
        sex = ["M", "F", "O", "U"]
        mails= ['gmail.com', 'hotmail.com', 'yahoo.com', 'icloud.com']
        numberrn = str(random.randint(10, 99))
        email = firstname + lastname + numberrn + "@" + random.choice(mails)
        race = {"1002-5": "American Indian or Alaska Native",	
                "2028-9":"Asian",
                "2054-5":"Black or African American",
                "2076-8": "Native Hawaiian or Other Pacific Islander",
                "2106-3":"White", "2131-1":"Other Race"}
        patRace = random.choice(race)
        raceCode = list(race.keys())
        patRaceCode = random.choice(raceCode)

        # generating address
        address = fake.street_address()
        building_number = fake.building_number()
        city = fake.city()
        state_abbr = fake.state_abbr()
        zip_code = fake.zipcode()
        country = fake.country()
        # PIDvalueGenerate()
        pid1 = pid1  # PID.1 - Set ID - PID
        pid2 = pid2  # PID.2 - Patient ID
        pid2_1 = pid2_1  # PID.2.1 - Id Number
        pid2_2 = pid2_2  # PID.2.2 - Check Digit
        pid2_3 = pid2_3  # PID.2.3 - Check Digit Scheme
        pid2_4 = pid2_4  # PID.2.4 - Assigning Authority
        pid2_5 = pid2_5  # PID.2.5 - Identifier Type Code
        pid2_6 = pid2_6  # PID.2.6 - Assigning Facility
        pid2_7 = pid2_7  # PID.2.7 - Effective Date
        pid2_8 = pid2_8  # PID.2.8 - Expiration Date
        pid2_9 = pid2_9  # PID.2.9 - Assigning Jurisdiction
        pid2_10 = pid2_10  # PID.2.10 - Assigning Agency Or Department
        pid3 = pid3  # PID.3 - Patient Identifier List
        pid3_1 = pid3_1  # PID.3.1 - Id Number
        pid3_2 = pid3_2  # PID.3.2 - Check Digit
        pid3_3 = pid3_3  # PID.3.3 - Check Digit Scheme
        pid3_4 = pid3_4  # PID.3.4 - Assigning Authority
        pid3_5 = pid3_5  # PID.3.5 - Identifier Type Code
        pid3_6 = pid3_6  # PID.3.6 - Assigning Facility
        pid3_7 = pid3_7  # PID.3.7 - Effective Date
        pid3_8 = pid3_8  # PID.3.8 - Expiration Date
        pid3_9 = pid3_9  # PID.3.9 - Assigning Jurisdiction
        pid3_10 = pid3_10  # PID.3.10 - Assigning Agency Or Department
        pid4 = pid4  # PID.4 - Alternate Patient ID
        pid4_1 = pid4_1  # PID.4.1 - Id Number
        pid4_2 = pid4_2  # PID.4.2 - Check Digit
        pid4_3 = pid4_3  # PID.4.3 - Check Digit Scheme
        pid4_4 = pid4_4  # PID.4.4 - Assigning Authority
        pid4_5 = pid4_5  # PID.4.5 - Identifier Type Code
        pid4_6 = pid4_6 # PID.4.6 - Assigning Facility
        pid4_7 = pid4_7 # PID.4.7 - Effective Date
        pid4_8 = pid4_8 # PID.4.8 - Expiration Date
        pid4_9 = pid4_9 # PID.4.9 - Assigning Jurisdiction
        pid4_10 = pid4_10 # PID.4.10 - Assigning Agency Or Department
        pid5 = pid5 # PID.5 - Patient Name
        pid5_1 = pid5_1 # PID.5.1 - Family Name
        pid5_2 = pid5_2 # PID.5.2 - Given Name
        pid5_3 = pid5_3 # PID.5.3 - Second And Further Given Names Or Initials Thereof
        pid5_4 = pid5_4 # PID.5.4 - Suffix (e.g., Jr Or Iii)
        pid5_5 = pid5_5 # PID.5.5 - Prefix (e.g., Dr)
        pid5_6 = pid5_6 # PID.5.6 - Degree (e.g., Md)
        pid5_7 = pid5_7 # PID.5.7 - Name Type Code
        pid5_8 = pid5_8 # PID.5.8 - Name Representation Code
        pid5_9 = pid5_9 # PID.5.9 - Name Context
        pid5_10 = pid5_10 # PID.5.10 - Name Validity Range
        pid5_11 = pid5_11 # PID.5.11 - Name Assembly Order
        pid5_12 = pid5_12 # PID.5.12 - Effective Date
        pid5_13 = pid5_13 # PID.5.13 - Expiration Date
        pid5_14 = pid5_14 # PID.5.14 - Professional Suffix
        pid6 = pid6 # PID.6 - Mother's Maiden Name
        pid6_1 = pid6_1 # PID.6.1 - Family Name
        pid6_2 = pid6_2 # PID.6.2 - Given Name
        pid6_3 = pid6_3 # PID.6.3 - Second And Further Given Names Or Initials Thereof
        pid6_4 = pid6_4 # PID.6.4 - Suffix (e.g., Jr Or Iii)
        pid6_5 = pid6_5 # PID.6.5 - Prefix (e.g., Dr)
        pid6_6 = pid6_6 # PID.6.6 - Degree (e.g., Md)
        pid6_7 = pid6_7 # PID.6.7 - Name Type Code
        pid6_8 = pid6_8 # PID.6.8 - Name Representation Code
        pid6_9 = pid6_9 # PID.6.9 - Name Context
        pid6_10 = pid6_10 # PID.6.10 - Name Validity Range
        pid6_11 = pid6_11 # PID.6.11 - Name Assembly Order
        pid6_12 = pid6_12 # PID.6.12 - Effective Date
        pid6_13 = pid6_13 # PID.6.13 - Expiration Date
        pid6_14 = pid6_14 # PID.6.14 - Professional Suffix
        pid7 = pid7 # PID.7 - Date/Time of Birth
        pid7_1 = pid7_1 # PID.7.1 - Time
        pid7_2 = pid7_2 # PID.7.2 - Degree Of Precision
        pid8 = pid8 # PID.8 - Administrative Sex
        pid9 = pid9 # PID.9 - Patient Alias
        pid9_1 = pid9_1 # PID.9.1 - Family Name
        pid9_2 = pid9_2 # PID.9.2 - Given Name
        pid9_3 = pid9_3 # PID.9.3 - Second And Further Given Names Or Initials Thereof
        pid9_4 = pid9_4 # PID.9.4 - Suffix (e.g., Jr Or Iii)
        pid9_5 = pid9_5 # PID.9.5 - Prefix (e.g., Dr)
        pid9_6 = pid9_6 # PID.9.6 - Degree (e.g., Md)
        pid9_7 = pid9_7 # PID.9.7 - Name Type Code
        pid9_8 = pid9_8 # PID.9.8 - Name Representation Code
        pid9_9 = pid9_9 # PID.9.9 - Name Context
        pid9_10 = pid9_10 # PID.9.10 - Name Validity Range
        pid9_11 = pid9_11 # PID.9.11 - Name Assembly Order
        pid9_12 = pid9_12 # PID.9.12 - Effective Date
        pid9_13 = pid9_13 # PID.9.13 - Expiration Date
        pid9_14 = pid9_14 # PID.9.14 - Professional Suffix
        pid10 = pid10 # PID.10 - Race
        pid10_1 = pid10_1 # PID.10.1 - Identifier
        pid10_2 = pid10_2 # PID.10.2 - Text
        pid10_3 = pid10_3 # PID.10.3 - Name Of Coding System
        pid10_4 = pid10_4 # PID.10.4 - Alternate Identifier
        pid10_4 = pid10_4 # PID.10.4 - Alternate Identifier
        pid10_5 = pid10_5 # PID.10.5 - Alternate Text
        pid10_6 = pid10_6 # PID.10.6 - Name Of Alternate Coding System
        pid11 = pid11 # PID.11 - Patient Address
        pid11_1 = pid11_1 # PID.11.1 - Street Address
        pid11_2 = pid11_2 # PID.11.2 - Other Designation
        pid11_3 = pid11_3 # PID.11.3 - City
        pid11_4 = pid11_4 # PID.11.4 - State Or Province
        pid11_5 = pid11_5 # PID.11.5 - Zip Or Postal Code
        pid11_6 = pid11_6 # PID.11.6 - Country
        pid11_7 = pid11_7 # PID.11.7 - Address Type
        pid11_8 = pid11_8 # PID.11.8 - Other Geographic Designation
        pid11_9 = pid11_9 # PID.11.9 - County/Parish Code
        pid11_10 = pid11_10 # PID.11.10 - Census Tract
        pid11_11 = pid11_11 # PID.11.11 - Address Representation Code
        pid11_12 = pid11_12 # PID.11.12 - Address Validity Range
        pid11_13 = pid11_13 # PID.11.13 - Effective Date
        pid11_14 = pid11_14 # PID.11.14 - Expiration Date
        pid12 = pid12 # PID.12 - County Code
        pid13 = pid13 # PID.13 - Phone Number - Home
        pid13_1 = pid13_1 # PID.13.1 - Telephone Number
        pid13_2 = pid13_2 # PID.13.2 - Telecommunication Use Code
        pid13_3 = pid13_3 # PID.13.3 - Telecommunication Equipment Type
        pid13_4 = pid13_4 # PID.13.4 - Email Address
        pid13_5 = pid13_5 # PID.13.5 - Country Code
        pid13_6 = pid13_6 # PID.13.6 - Area/City Code
        pid13_7 = pid13_7 # PID.13.7 - Local Number
        pid13_8 = pid13_8 # PID.13.8 - Extension
        pid13_9 = pid13_9 # PID.13.9 - Any Text
        pid13_10 = pid13_10 # PID.13.10 - Extension Prefix
        pid13_11 = pid13_11 # PID.13.11 - Speed Dial Code
        pid13_12 = pid13_12 # PID.13.12 - Unformatted Telephone Number
        pid14 = pid14 # PID.14 - Phone Number - Business
        pid14_1 = pid14_1 # PID.14.1 - Telephone Number
        pid14_2 = pid14_2 # PID.14.2 - Telecommunication Use Code
        pid14_3 = pid14_3 # PID.14.3 - Telecommunication Equipment Type
        pid14_4 = pid14_4 # PID.14.4 - Email Address
        pid14_5 = pid14_5 # PID.14.5 - Country Code
        pid14_6 = pid14_6 # PID.14.6 - Area/City Code
        pid14_7 = pid14_7 # PID.14.7 - Local Number
        pid14_8 = pid14_8 # PID.14.8 - Extension
        pid14_9 = pid14_9 # PID.14.9 - Any Text
        pid14_10 = pid14_10 # PID.14.10 - Extension Prefix
        pid14_11 = pid14_11 # PID.14.11 - Speed Dial Code
        pid14_12 = pid14_12 # PID.14.12 - Unformatted Telephone Number
        pid15 = pid15 # PID.15 - Primary Language
        pid15_1 = pid15_1 # PID.15.1 - Identifier
        pid15_2 = pid15_2 # PID.15.2 - Text
        pid15_3 = pid15_3 # PID.15.3 - Name Of Coding System
        pid15_4 = pid15_4 # PID.15.4 - Alternate Identifier
        pid15_5 = pid15_5 # PID.15.5 - Alternate Text
        pid15_6 = pid15_6 # PID.15.6 - Name Of Alternate Coding System
        pid16 = pid16 # PID.16 - Marital Status
        pid16_1 = pid16_1 # PID.16.1 - Identifier
        pid16_2 = pid16_2 # PID.16.2 - Text
        pid16_3 = pid16_3 # PID.16.3 - Name Of Coding System
        pid16_4 = pid16_4 # PID.16.4 - Alternate Identifier
        pid16_5 = pid16_5 # PID.16.5 - Alternate Text
        pid16_6 = pid16_6 # PID.16.6 - Name Of Alternate Coding System
        pid17 = pid17 # PID.17 - Religion 
        pid17_1 = pid17_1 # PID.17.1 - Identifier
        pid17_2 = pid17_2 # PID.17.2 - Text
        pid17_3 = pid17_3 # PID.17.3 - Name Of Coding System
        pid17_4 = pid17_4 # PID.17.4 - Alternate Identifier
        pid17_5 = pid17_5 # PID.17.5 - Alternate Text
        pid17_6 = pid17_6 # PID.17.6 - Name Of Alternate Coding System
        pid18 = pid18 # PID.18 - Patient Account Number
        pid18_1 = pid18_1 # PID.18.1 - Id Number
        pid18_2 = pid18_2 # PID.18.2 - Check Digit
        pid18_3 = pid18_3 # PID.18.3 - Check Digit Scheme
        pid18_4 = pid18_4 # PID.18.4 - Assigning Authority
        pid18_5 = pid18_5 # PID.18.5 - Identifier Type Code
        pid18_6 = pid18_6 # PID.18.6 - Assigning Facility
        pid18_7 = pid18_7 # PID.18.7 - Effective Date
        pid18_8 = pid18_8 # PID.18.8 - Expiration Date
        pid18_9 = pid18_9 # PID.18.9 - Assigning Jurisdiction
        pid18_10 = pid18_10 # PID.18.10 - Assigning Agency Or Department
        pid19 = pid19 # PID.19 - SSN Number - Patient
        pid20 = pid20 # PID.20 - Driver's License Number - Patient
        pid20_1 = pid20_1 # PID.20.1 - License Number
        pid20_2 = pid20_2 # PID.20.2 - Issuing State, Province, Country
        pid20_3 = pid20_3 # PID.20.3 - Expiration Date
        pid21 = pid21 # PID.21 - Mother's Identifier
        pid22 = pid22 # PID.22 - Ethnic Group
        pid23 = pid23 # PID.23 - Birth Place
        pid24 = pid24 # PID.24 - Multiple Birth Indicator
        pid25 = pid25 # PID.25 - Birth Order
        pid26 = pid26 # PID.26 - Citizenship
        pid27 = pid27 # PID.27 - Veterans Military Status
        pid28 = pid28 # PID.28 - Nationality
        pid29 = pid29 # PID.29 - Patient Death Date and Time
        pid30 = pid30 # PID.30 - Patient Death Indicator
        pid31 = pid31 # PID.31 - Identity Unknown Indicator
        pid32 = pid32 # PID.32 - Identity Reliability Code
        pid33 = pid33 # PID.33 - Last Update Date/Time
        pid34 = pid34 # PID.34 - Last Update Facility
        pid35 = pid35 # PID.35 - Species Code
        pid36 = pid36 # PID.36 - Breed Code
        pid37 = pid37 # PID.37 - Strain
        pid38 = pid38 # PID.38 - Production Class Code
        pid39 = pid39 # PID.39 - Tribal Citizenship
        PID = (
        f"PID|"
        f"{pid1}|{pid2_1}^{pid2_2}^{pid2_3}|{pid3}^{pid3_1}^{pid3_2}^{pid3_3}|"
        f"{pid4}^{pid4_1}^{pid4_2}^{pid4_3}^{pid4_4}^{pid4_5}^{pid4_6}^{pid4_7}^{pid4_8}^{pid4_9}^{pid4_10}|"
        f"{pid5}^{pid5_1}^{pid5_2}^{pid5_3}^{pid5_4}^{pid5_5}^{pid5_6}^{pid5_7}^{pid5_8}^{pid5_9}^{pid5_10}^{pid5_11}|"
        f"{pid5_12}^{pid5_13}^{pid5_14}|{pid6}^{pid6_1}^{pid6_2}^{pid6_3}^{pid6_4}^{pid6_5}^{pid6_6}^{pid6_7}^{pid6_8}^{pid6_9}^{pid6_10}^{pid6_11}|"
        f"{pid6_12}^{pid6_13}^{pid6_14}|{pid7}^{pid7_1}^{pid7_2}|{pid8}|{pid9}^{pid9_1}^{pid9_2}^{pid9_3}^{pid9_4}^{pid9_5}^{pid9_6}^{pid9_7}^{pid9_8}^{pid9_9}^{pid9_10}|"
        f"{pid9_11}^{pid9_12}^{pid9_13}^{pid9_14}|{pid10}^{pid10_1}^{pid10_2}^{pid10_3}^{pid10_4}^{pid10_5}^{pid10_6}|{pid11_1}^{pid11_2}^{pid11_3}^{pid11_4}^{pid11_5}|"
        f"{pid11_6}^{pid11_7}^{pid11_8}^{pid11_9}^{pid11_10}^{pid11_11}^{pid11_12}^{pid11_13}^{pid11_14}|{pid12}|{pid13}^{pid13_1}^{pid13_2}^{pid13_3}^{pid13_4}|"
        f"{pid13_5}^{pid13_6}^{pid13_7}^{pid13_8}^{pid13_9}^{pid13_10}^{pid13_11}^{pid13_12}|{pid14}^{pid14_1}^{pid14_2}^{pid14_3}^{pid14_4}^{pid14_5}^{pid14_6}^{pid14_7}|"
        f"{pid14_8}^{pid14_9}^{pid14_10}^{pid14_11}^{pid14_12}|{pid15}^{pid15_1}^{pid15_2}^{pid15_3}^{pid15_4}^{pid15_5}^{pid15_6}|{pid16}^{pid16_1}^{pid16_2}^{pid16_3}^{pid16_4}|"
        f"{pid16_5}^{pid16_6}|{pid17}^{pid17_1}^{pid17_2}^{pid17_3}^{pid17_4}^{pid17_5}^{pid17_6}|{pid18}^{pid18_1}^{pid18_2}^{pid18_3}^{pid18_4}^{pid18_5}^{pid18_6}^{pid18_7}|"
        f"{pid18_8}^{pid18_9}^{pid18_10}|{pid19}|{pid20}^{pid20_1}^{pid20_2}^{pid20_3}|{pid21}|{pid22}|{pid23}|{pid24}|{pid25}|{pid26}|{pid27}|{pid28}|{pid29}|{pid30}|"
        f"{pid31}|{pid32}|{pid33}|{pid34}|{pid35}|{pid36}|{pid37}|{pid38}|{pid39}")
        return PID

    def PD1(self):
        pass
    def NTE(self):
        pass
    def NK1(self):
        pass
    def PV1(self):
        pass 
    def PV1():
        pv1_1 = pv1_1 # PV1.1 - Set ID - PV1
        pv1_2 = pv1_2 # PV1.2 - Patient Class ----- R
        pv1_3 = pv1_3 # PV1.3 - Assigned Patient Location
        pv1_4 = pv1_4 # PV1.4 - Admission Type
        pv1_5 = pv1_5 # PV1.5 - Preadmit Number
        pv1_6 = pv1_6 # PV1.6 - Prior Patient Location
        pv1_7 = pv1_7 # PV1.7 - Attending Doctor
        pv1_8 = pv1_8 # PV1.8 - Referring Doctor
        pv1_9 = pv1_9 # PV1.9 - Consulting Doctor
        pv1_10 = pv1_10 # PV1.10 - Hospital Service
        pv1_11 = pv1_11 # PV1.11 - Temporary Location
        pv1_12 = pv1_12 # PV1.12 - Preadmit Test Indicator
        pv1_13 = pv1_13 # PV1.13 - Re-admission Indicator
        pv1_14 = pv1_14 # PV1.14 - Admit Source
        pv1_15 = pv1_15 # PV1.15 - Ambulatory Status
        pv1_16 = pv1_16 # PV1.16 - VIP Indicator
        pv1_17 = pv1_17 # PV1.17 - Admitting Doctor
        pv1_18 = pv1_18 # PV1.18 - Patient Type
        pv1_19 = pv1_19 # PV1.19 - Visit Number
        pv1_20 = pv1_20 # PV1.20 - Financial Class
        pv1_21 = pv1_21 # PV1.21 - Charge Price Indicator
        pv1_22 = pv1_22 # PV1.22 - Courtesy Code
        pv1_23 = pv1_23 # PV1.23 - Credit Rating
        pv1_24 = pv1_24 # PV1.24 - Contract Code
        pv1_25 = pv1_25 # PV1.25 - Contract Effective Date
        pv1_26 = pv1_26 # PV1.26 - Contract Amount
        pv1_27 = pv1_27 # PV1.27 - Contract Period
        pv1_28 = pv1_28 # PV1.28 - Interest Code
        pv1_29 = pv1_29 # PV1.29 - Transfer to Bad Debt Code
        pv1_30 = pv1_30 # PV1.30 - Transfer to Bad Debt Date
        pv1_31 = pv1_31 # PV1.31 - Bad Debt Agency Code
        pv1_32 = pv1_32 # PV1.32 - Bad Debt Transfer Amount
        pv1_33 = pv1_33 # PV1.33 - Bad Debt Recovery Amount
        pv1_34 = pv1_34 # PV1.34 - Delete Account Indicator
        pv1_35 = pv1_35 # PV1.35 - Delete Account Date
        pv1_36 = pv1_36 # PV1.36 - Discharge Disposition
        pv1_37 = pv1_37 # PV1.37 - Discharged to Location
        pv1_38 = pv1_38 # PV1.38 - Diet Type
        pv1_39 = pv1_39 # PV1.39 - Servicing Facility
        pv1_40 = pv1_40 # PV1.40 - Bed Status
        pv1_41 = pv1_41 # PV1.41 - Account Status
        pv1_42 = pv1_42 # PV1.42 - Pending Location
        pv1_43 = pv1_43 # PV1.43 - Prior Temporary Location
        pv1_44 = pv1_44 # PV1.44 - Admit Date/Time
        pv1_45 = pv1_45 # PV1.45 - Discharge Date/Time
        pv1_46 = pv1_46 # PV1.46 - Current Patient Balance
        pv1_47 = pv1_47 # PV1.47 - Total Charges
        pv1_48 = pv1_48 # PV1.48 - Total Adjustments
        pv1_49 = pv1_49 # PV1.49 - Total Payments
        pv1_50 = pv1_50 # PV1.50 - Alternate Visit ID
        pv1_51 = pv1_51 # PV1.51 - Visit Indicator
        pv1_52 = pv1_52 # PV1.52 - Other Healthcare Provider
        PV1 = ()
        return PV1
    
    def PV2(self):
        pass
    def ORC(self):
        pass
    def OBR(self):
        pass
    def NTE(self):
        pass
    def TQ1(self):
        pass
    def TQ2(self):
        pass
    def CTD(self):
        pass
    def OBX(self):
        pass
    def FT1(self):
        pass
    def CTI(self):
        pass
    def DSC(self):
        pass