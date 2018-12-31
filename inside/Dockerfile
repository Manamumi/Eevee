FROM python:3.7

RUN mkdir -p /inside_eevee
ADD . /inside_eevee/

WORKDIR /inside_eevee

RUN pip install -r requirements.txt

EXPOSE 7744

CMD ["python", "app.py"]
